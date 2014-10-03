#ifndef SUBMITDELEGATE_H
#define SUBMITDELEGATE_H

template <class F, class S>
class SubmitDelegate
{
public:
    SubmitDelegate(F* obj) : obj_(obj) { }
    bool viable(S* submission) {
        return obj_->viable(submission);
    }
    void submit(S* submission) {
        obj_->submit(submission);
    }
    
private:
    F* obj_;
};

#endif //SUBMITDELEGATE_H
