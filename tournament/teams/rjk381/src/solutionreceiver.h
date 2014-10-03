#ifndef SOLUTIONRECEIVER_H
#define SOLUTIONRECEIVER_H

#include <pthread.h>
#include <list>

#include "proposal.h"

class SolutionReceiver
{
public:
    SolutionReceiver(const char* output);
    SolutionReceiver(char* output);
    ~SolutionReceiver();

    //checks to see if the object is viable: submission is not altered
    //returns true if viable, false if unviable
    bool viable(Proposal* submission);

    //ownership of submission is transferred to this object: clients
    //should not expect a valid object at pointer position
    void submit(Proposal* submission);

    //blocks until queue is non-empty
    void wait_queue() { }

    //queue is empty: does nothing, ret false
    //queue is not empty: keeps better of queue's front proposal and
    //existing best proposal, deletes loser, ret true
    bool process_one();

    //consumes stored best proposal: no locks required, but can
    //only be called once
    void flush();

    int qs() {
        pthread_mutex_lock(&queue_m_);
        int s = queue_.size();
        pthread_mutex_unlock(&queue_m_);
        return s;
    }

private:
    void init();

    Proposal* best_proposal_;
    float best_length_;

    std::list<Proposal*> queue_;
    pthread_mutex_t queue_m_;

    char* output_;
    char* output_tmp_;
        
};

#endif //SOLUTION_RECEIVER_H
