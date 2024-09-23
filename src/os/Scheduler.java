package os;

import interrupt.FinishProgram;
import interrupt.Interrupt;
import process.Process;
import process.State;

import java.util.Comparator;
import java.util.List;

public class Scheduler {

    public Process schedule(List<Process> processes) {
        var scheduledProcess = processes.stream()
                .filter(process -> process.getState().equals(State.READY))
                .filter(process -> process.getCredits() > 0)
                .sorted(Comparator.comparing(Process::getPriority)
                        .thenComparing(Process::getOrder)
                        .thenComparing(Process::getCredits))
                .toList()
                .get(0);

        System.out.println("---------");
        System.out.printf("Process scheduled: %s%n", scheduledProcess.getName());
        System.out.println("---------");
        return scheduledProcess;
    }

    public void insertCredits(List<Process> processes) {
        processes
                .stream()
                .filter(process -> !process.getState().equals(State.EXIT))
                .forEach(process -> {
                    process.setCredits((process.getCredits() / 2) + process.creditsByPriority());
                    process.setState(State.READY);
                });
    }

    public void changeStatus(Process process, Interrupt interrupt) {
        switch (interrupt) {
            case IO_STARTED, BLOCK -> process.setState(State.BLOCKED);
            case IO_FINISHED, TIMEOUT -> process.setState(State.READY);
            case PROCESS_FINISHED -> process.setState(State.EXIT);
        }
    }

    public void verifyState(List<Process> processes) {
        var list = processes.stream()
                .filter(process -> !process.getState().equals(State.EXIT))
                .toList();

        if (list.isEmpty()) {
            throw new FinishProgram("Finish Program");
        }

        var nonBlockedList = processes.stream()
                .filter(process -> !process.getState().equals(State.BLOCKED) || !process.getState().equals(State.EXIT))
                .toList();

        if (nonBlockedList.isEmpty()) {
            insertCredits(processes);
        }
    }
}
