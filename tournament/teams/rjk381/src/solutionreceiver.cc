#include "solutionreceiver.h"

#include <string.h>
#include <float.h>
#include <stdio.h>

SolutionReceiver::SolutionReceiver(const char* output)
{
    int length = strlen(output);
    output_ = new char[length+1];
    output_tmp_ = new char[length+5];
    strncpy(output_, output, length+1);
    strncpy(output_tmp_, output, length+1);
    strncpy(output_tmp_+length, ".tmp\0", 5);
    init();
}

SolutionReceiver::SolutionReceiver(char* output)
{
    int length = strlen(output);
    output_ = new char[length+1];
    output_tmp_ = new char[length+5];
    strncpy(output_, output, length+1);
    strncpy(output_tmp_, output, length+1);
    strncpy(output_tmp_+length, ".tmp\0", 5);
    init();
}

void SolutionReceiver::init()
{
    best_proposal_ = NULL;
    best_length_ = FLT_MAX;
    pthread_mutex_init(&queue_m_, NULL);
}

SolutionReceiver::~SolutionReceiver()
{
    pthread_mutex_destroy(&queue_m_);
    delete [] output_;
    delete [] output_tmp_;
    // queue should also be emptied
}

bool SolutionReceiver::viable(Proposal* submission)
{
    return submission->path_length < best_length_;
}

//thread-safe
void SolutionReceiver::submit(Proposal* submission)
{
    //printf("Recevied submission for proposal with length %f\n", submission->path_length);
    if (submission->path_length < best_length_) {
        //printf("--> Adding to queue\n");
        pthread_mutex_lock(&queue_m_);
        queue_.push_back(submission);
        pthread_mutex_unlock(&queue_m_);
        //NOT THREAD SAFE best_length_ = submission->path_length; //don't allow queue entries that will be rejected before acceptance
    }
    else {
        //printf("--> Ignoring (not viable)\n");
        delete submission;
    }
}

//not thread-safe
bool SolutionReceiver::process_one()
{
    pthread_mutex_lock(&queue_m_);
    int size = queue_.size();
    pthread_mutex_unlock(&queue_m_);
    if (size > 0) {
        pthread_mutex_lock(&queue_m_);
        Proposal* p = queue_.front();
        //printf("Processing submission with length %f\n", p->path_length);
        queue_.pop_front();
        pthread_mutex_unlock(&queue_m_);

        if (p->path_length < best_length_) {
            //printf("Found best new proposal\n");
            Proposal* old_best = best_proposal_;
            if (old_best) {delete old_best;}
            best_proposal_ = p;
            best_length_ = best_proposal_->path_length;
        }
        else if (p != best_proposal_) {
            //printf("Deleting p\n");
            delete p;
        }
        else {
            //printf("Duplicate detected, not deleting\n");
        }
        return true;
    }
    else {
        //printf("No submission to process: skipping\n");
        return false;
    }
}

void SolutionReceiver::flush()
{
    Proposal* p = best_proposal_;
    best_proposal_ = NULL;

    if (p) {
        //printf("Flush: writing to [%s] -> [%s]\n", output_tmp_, output_);

        //printf("Ordering:\n");
        
        FILE* ftmp = fopen(output_tmp_, "w");
        const int s = p->record_count;
        for (int i = 0; i < s; ++i) {
            //printf("%d\n", p->record[i].id);
            fprintf(ftmp, "%d\n", p->record[i].id);
        }
        fclose(ftmp);

        printf("Flushed with path length: %f (source thread: %d)\n",
                p->path_length, p->owner_thread_id);

        int rc = rename(output_tmp_, output_);
        if (rc != 0) {
            printf("Rename failed: errcode %d\n", rc);
        }

        delete p;
        //printf("Flush: done\n");
    }
    else {
        //printf("Flush: no p found, skipping\n");
    }
}

