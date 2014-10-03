##################################
###                            ###
###  Compilation Rules         ###
###                            ### 
##################################

# Common binaries
GCC = g++

# CCFLAGS = -g
LDFLAGS = -lpthread

default: all

# Target object
MAINTARGET = tspsolver

# Source file directory
SRCDIR := src

# Object file directory
OBJDIR := obj
$(OBJDIR) :
	mkdir -p $@
	mkdir -p $@/lib

# Compiler commands
$(OBJDIR)/%.o : $(SRCDIR)/%.cc
	$(GCC) $(CCFLAGS) -c $< -o $@ $(LDFLAGS)

# Archive options
ARCHIVE_OPTIONS = rucsv


##################################
###                            ###
###  Libraries                 ###
###                            ### 
##################################

# Main source file
MAINSRC = $(SRCDIR)/main.cc

# Combined libraries
LIB_ALL = $(MAINSRC)

# Proposal
OBJ_PROPOSAL_FILES = proposal.cc
OBJ_PROPOSAL_OBJ = $(OBJ_PROPOSAL_FILES:%.cc=$(OBJDIR)/%.o)
OBJ_PROPOSAL_TARGET = $(OBJDIR)/objproposal.a
$(OBJ_PROPOSAL_TARGET) : $(OBJ_PROPOSAL_OBJ)
	ar $(ARCHIVE_OPTIONS) $@ $+ 
LIB_ALL += $(OBJ_PROPOSAL_TARGET)

# Solution Receiver
OBJ_SOLUTIONRECEIVER_FILES = solutionreceiver.cc
OBJ_SOLUTIONRECEIVER_OBJ = $(OBJ_SOLUTIONRECEIVER_FILES:%.cc=$(OBJDIR)/%.o)
OBJ_SOLUTIONRECEIVER_TARGET = $(OBJDIR)/objsolutionreceiver.a
$(OBJ_SOLUTIONRECEIVER_TARGET) : $(OBJ_SOLUTIONRECEIVER_OBJ)
	ar $(ARCHIVE_OPTIONS) $@ $+ 
LIB_ALL += $(OBJ_SOLUTIONRECEIVER_TARGET)

# Solvers
OBJ_SOLVERS_FILES = solvers.cc
OBJ_SOLVERS_OBJ = $(OBJ_SOLVERS_FILES:%.cc=$(OBJDIR)/%.o)
OBJ_SOLVERS_TARGET = $(OBJDIR)/objsolvers.a
$(OBJ_SOLVERS_TARGET) : $(OBJ_SOLVERS_OBJ)
	ar $(ARCHIVE_OPTIONS) $@ $+ 
LIB_ALL += $(OBJ_SOLVERS_TARGET)

# # Simulator
# OBJ_SIMULATOR_FILES = simulator.cc
# OBJ_SIMULATOR_OBJ = $(OBJ_SIMULATOR_FILES:%.cc=$(OBJDIR)/%.o)
# OBJ_SIMULATOR_TARGET = $(OBJDIR)/objsimulator.a
# $(OBJ_SIMULATOR_TARGET) : $(OBJ_SIMULATOR_OBJ)
# 	ar $(ARCHIVE_OPTIONS) $@ $+ 
# LIB_ALL += $(OBJ_SIMULATOR_TARGET)
# 
# # Scheduler
# OBJ_SCHEDULER_FILES = scheduler.cc
# OBJ_SCHEDULER_OBJ = $(OBJ_SCHEDULER_FILES:%.cc=$(OBJDIR)/%.o)
# OBJ_SCHEDULER_TARGET = $(OBJDIR)/objscheduler.a
# $(OBJ_SCHEDULER_TARGET) : $(OBJ_SCHEDULER_OBJ)
# 	ar $(ARCHIVE_OPTIONS) $@ $+ 
# LIB_ALL += $(OBJ_SCHEDULER_TARGET)


##################################
###                            ###
###  Target rules              ###
###                            ### 
##################################

all: build

build: $(OBJDIR) $(MAINTARGET)

$(MAINTARGET) : $(LIB_ALL)
	$(GCC) $(CCFLAGS) -o $@ $+ $(LDFLAGS)

run: build
	./$(MAINTARGET)

clean:
	rm -rf $(OBJDIR)

clobber: 
	rm -rf $(OBJDIR)
	rm -f $(MAINTARGET)
