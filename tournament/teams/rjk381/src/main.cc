#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
#include <time.h>
#include <unistd.h>

#include "proposal.h"
#include "submitdelegate.h"
#include "solutionreceiver.h"
#include "solvers.h"

#define WORKER_THREAD_COUNT 8
#define EXEC_ENGINE Solvers::solver_three

using Solvers::compute_data;

void* cdata_thread(void* cdata_v)
{
    compute_data& cdata = *((compute_data*)cdata_v);
    cdata.exec(cdata);
    pthread_exit(NULL);
}

int main(int argc, const char** argv)
{
    if (argc != 3) {
        printf("Usage: %s input output\n", argv[0]);
        exit(-1);
    }

    srand(time(NULL));

    Proposal* p = ProposalHelpers::proposal_from_file(argv[1]);
    printf("loaded set with size: %d\n", p->record_count);

    SolutionReceiver rec(argv[2]);
    SDSP sd(&rec);

    void* c_raw = operator new[]((WORKER_THREAD_COUNT)*sizeof(compute_data));
    compute_data* c = static_cast<compute_data*>(c_raw);
    for (int i = 0; i < (WORKER_THREAD_COUNT); ++i) {
        new(&c[i])compute_data(sd);
        c[i].initial_proposal = p;
        c[i].exec = *((EXEC_ENGINE));
        c[i].id = i;
    }

    pthread_t t[WORKER_THREAD_COUNT];
    int rc;
    for (int i = 0; i < (WORKER_THREAD_COUNT); ++i) {
        rc = pthread_create(&t[i], NULL, cdata_thread, &c[i]);
    }

    while (true) {
        usleep(2000000);
        printf("=================\n");
        if (rec.qs() > 1) printf("Queue size: %d\n", rec.qs());
        while (rec.process_one()) { }
        rec.flush();
        for (int i = 0; i < (WORKER_THREAD_COUNT); ++i) {
            printf("Status %d: %f\n", i, c[i].best_path_length);
        }
    }

    void* status;
    for (int i = 0; i < (WORKER_THREAD_COUNT); ++i) {
        rc = pthread_join(t[i], &status);
    }

    for (int i = (WORKER_THREAD_COUNT)-1; i >= 0; --i) {
        c[i].~compute_data();
    }
    operator delete[](c_raw);
    
    pthread_exit(NULL);
}

