#ifndef PROPOSAL_H
#define PROPOSAL_H

#include <algorithm>

struct CityRecord
{
    int id;
    float x;
    float y;
};

struct Proposal
{
    int owner_thread_id;
    float path_length;
    CityRecord* record;
    int record_count;

    Proposal(int size) {
        record_count = size;
        record = new CityRecord[record_count];
    }
    ~Proposal() {
        delete [] record;
    }
    Proposal(Proposal& other) :
            record_count(other.record_count),
            path_length(other.path_length),
            owner_thread_id(other.owner_thread_id) {
        record = new CityRecord[record_count];
        for (int i = 0; i < record_count; i++) {
            record[i] = other.record[i];
        }
    }
    Proposal& operator=(Proposal tmp) {
        std::swap(*this, tmp);
        return *this;
    }
};

namespace ProposalHelpers {
    Proposal* proposal_from_file(const char* fname);
    float eval(Proposal* p);
}

#endif //PROPOSAL_H
