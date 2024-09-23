package interrupt;

import process.Process;
import process.ProcessType;
import process.State;
import scheduler.Scheduler;

import java.util.List;

public class Hardware {

    private final Scheduler scheduler;

    public Hardware(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void requestIO(Process process) {
        scheduler.changeStatus(process, Interrupt.IO_STARTED);
    }

    public void timeout(Process process) {
        scheduler.changeStatus(process, Interrupt.TIMEOUT);
    }

    public void responseIO(Process process) {
        scheduler.changeStatus(process, Interrupt.IO_FINISHED);
    }

    public void terminate(Process process) {
        scheduler.changeStatus(process, Interrupt.PROCESS_FINISHED);
    }

    public void block(Process process) {
        scheduler.changeStatus(process, Interrupt.BLOCK);
    }

    public void unschedule(Process process, ProcessType type) {
        switch (type) {
            case IO -> throw new RequestIO("Request IO");
            case CPU -> terminate(process);
        }
    }

    public void processIO(List<Process> processes) {
        processes.stream()
                .filter(process -> process.getState().equals(State.BLOCKED))
                .forEach(Process::awaitIO);
    }
}

