#include "proposal.h"

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

//namespace ProposalHelpers {

Proposal* ProposalHelpers::proposal_from_file(const char* fname)
{
    FILE* fp = fopen(fname, "r");
    if (!fp) {
        printf("Failure: could not open file %s\n", fname);
        exit(-2);
    }

    //get size
    int size = 0;
    const int buflen = 512;
    char buf[buflen];
    while (fgets(buf, buflen, fp)) {
        if (strlen(buf) > 1) {
            size++;
        }
    }
    //printf("size: %d\n", size);
    rewind(fp);

    //create object and add to buffer
    Proposal* p = new Proposal(size);
    
    char* token;
    char* r;
    int i = 0;
    while (fgets(buf, buflen, fp)) {
        if (strlen(buf) > 1) {
            token = strtok_r(buf, " ", &r);
            p->record[i].id = atoi(token);
            token = strtok_r(NULL, " ", &r);
            p->record[i].x = atof(token);
            token = strtok_r(NULL, " ", &r);
            p->record[i].y = atof(token);
            //printf("imported id: %d x: %f y: %f\n", p->record[i].id, p->record[i].x, p->record[i].y);
            i++; 
        }
    }

    p->path_length = ProposalHelpers::eval(p);
    return p;
}

float ProposalHelpers::eval(Proposal* p)
{
    float len = 0;
    int s = p->record_count;
    float dx, dy;
    int i, j;
    for (i = 0, j = 1; j < s; ++i, ++j) {
        dx = p->record[i].x - p->record[j].x;
        dy = p->record[i].y - p->record[j].y;
        len += sqrt(dx*dx + dy*dy);
    }
    dx = p->record[i].x - p->record[0].x;
    dy = p->record[i].y - p->record[0].y;
    len += sqrt(dx*dx + dy*dy);
    return len;
}

