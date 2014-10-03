#ifndef SOLVERS_H
#define SOLVERS_H

#include "proposal.h"
#include "submitdelegate.h"
#include "solutionreceiver.h"

typedef SubmitDelegate<SolutionReceiver, Proposal> SDSP;

namespace Solvers {
    struct compute_data
    {
        compute_data(SDSP& sd_) : sd(sd_) { }
        int id;
        SDSP& sd;
        Proposal* initial_proposal;
        float best_path_length;
        void (*exec)(compute_data&);
    };

    void generate_noise_submission(compute_data& cdata);
    void random_swaps(compute_data& cdata);
    void solver_three(compute_data& cdata);

}

#endif //SOLVERS_H
