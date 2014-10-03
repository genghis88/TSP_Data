#include "solvers.h"

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <algorithm>

void Solvers::generate_noise_submission(compute_data& cdata)
{
    int size = cdata.initial_proposal->record_count;

    while (true) {
        Proposal* p = new Proposal(size);
        p->path_length = rand() % 1000000;
        p->record[0].id = p->path_length;
        for (int i = 1; i < size; ++i) {
            p->record[i].id = i;
        }
        cdata.sd.submit(p);
        usleep(250);
    }
}

void Solvers::random_swaps(compute_data& cdata)
{
    int size = cdata.initial_proposal->record_count;
    Proposal* p = new Proposal(*cdata.initial_proposal);

    while (true) {
        int it = (int)(rand() % 500);
        for (int i = 0; i < it; ++i) {
            int j = (int)(rand() % size);
            int k = (int)(rand() % size);
            CityRecord t = p->record[j];
            p->record[j] = p->record[k];
            p->record[k] = t;
        }
        p->path_length = ProposalHelpers::eval(p);
        if (cdata.sd.viable(p)) {
            Proposal* s = new Proposal(*p);
            cdata.sd.submit(s);
        }
    }
}

namespace SolverHelpers {
    void hello(int id) {
        printf("Solver %d online\n", id);
    }

    //i and k are both inclusive
    void inplace_2opt(Proposal& p, int a, int b) {
        for (;a<b;++a,--b) {
            std::swap(p.record[a], p.record[b]);
        }
    }

    void shuffle_random(Proposal& p, int times) {
        int i, j;
        int size = p.record_count;
        for (;times > 0;--times) {
            i = (int)rand() % size;
            j = (int)rand() % size;
            std::swap(p.record[i], p.record[j]);
        }
    }

}

//"brute force" iterated 2-opt chains
void Solvers::solver_three(compute_data& cdata)
{
    SolverHelpers::hello(cdata.id);

    int size = cdata.initial_proposal->record_count;
    Proposal* p = new Proposal(*cdata.initial_proposal);
    p->owner_thread_id = cdata.id;
    Proposal* best = new Proposal(*p);


    //lower: faster convergence
    //higher: less likely to get stuck in local optima
    const int round_tries = 5;
    int round_duration = 100 * 500;// * 500;
    int round = 0;

    while (true) {
        for (int tries = 0; tries < round_tries; ++tries) {
            int i = (int)rand() % size;
            int j = (int)rand() % size;
            SolverHelpers::inplace_2opt(*p, i, j);
            p->path_length = ProposalHelpers::eval(p);
            //printf("Solver %d: path length %f\n", cdata.id, p->path_length);
            if (p->path_length < best->path_length) {
                delete best;
                best = new Proposal(*p);
                tries = 0;
                round = 0;
                cdata.best_path_length = best->path_length;
                //printf("Solver %d: new best %f\n", cdata.id, p->path_length);
                if (cdata.sd.viable(best)) {
                    Proposal* s = new Proposal(*best);
                    cdata.sd.submit(s);
                }
            }
        }

        round++;
        if (round >= round_duration) {
            round = 0;
            delete best;
            best = new Proposal(*cdata.initial_proposal);
            best->owner_thread_id = cdata.id;
            best->path_length = ProposalHelpers::eval(best);
            cdata.best_path_length = best->path_length;
        }

        //printf("Solver %d: reverting %f -> %f\n", cdata.id, p->path_length, best->path_length);
        delete p;
        p = new Proposal(*best);
    }
}

