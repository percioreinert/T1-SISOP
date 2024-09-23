package interrupt;

import os.OperationalSystem;
import process.Process;
import process.ProcessType;
import process.State;

import java.util.List;

public class Hardware {

    private final OperationalSystem operationalSystem;

    public Hardware(OperationalSystem operationalSystem) {
        this.operationalSystem = operationalSystem;
    }

    public void requestIO(Process process) {
        operationalSystem.getScheduler().changeStatus(process, Interrupt.IO_STARTED);
        System.out.println("---------");
        System.out.printf("Process interrupted by IO call: %s%n", process.getName());
        System.out.println("---------");
    }

    public void timeout(Process process) {
        if (process.getCredits() != 0) {
            operationalSystem.getScheduler().changeStatus(process, Interrupt.TIMEOUT);
            System.out.println("---------");
            System.out.printf("Process interrupted by timeout: %s%n", process.getName());
            System.out.println("---------");
        }
    }

    public void responseIO(Process process) {
        operationalSystem.getScheduler().changeStatus(process, Interrupt.IO_FINISHED);
        System.out.println("---------");
        System.out.printf("Process ready after IO: %s%n", process.getName());
        System.out.println("---------");
    }

    public void terminate(Process process) {
        operationalSystem.getScheduler().changeStatus(process, Interrupt.PROCESS_FINISHED);
        System.out.println("---------");
        System.out.printf("Process finished: %s%n", process.getName());
        System.out.println("---------");
    }

    public void block(Process process) {
        operationalSystem.getScheduler().changeStatus(process, Interrupt.BLOCK);
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

