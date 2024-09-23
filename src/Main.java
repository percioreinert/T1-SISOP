import interrupt.FinishProgram;
import interrupt.Hardware;
import interrupt.RequestIO;
import process.Process;
import process.*;
import scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        var scheduler = new Scheduler();
        var hardware = new Hardware(scheduler);
        var processes = initialize(hardware);

        Process scheduledProcess;
        while (true) {
            try {
                scheduler.verifyState(processes);
                scheduledProcess = scheduler.schedule(processes);
                compute(scheduledProcess, hardware);
                hardware.processIO(processes);
            } catch (IndexOutOfBoundsException e) {
                scheduler.insertCredits(processes);
            } catch (FinishProgram e) {
                exit(0);
            }
        }
    }

    private static void compute(Process process, Hardware hardware) throws InterruptedException {
        Thread.sleep(1);
        try {
            process.consumeCredit();
            process.consumeCPUTime();
            process.lowerPriority();
            showState(process);
            hardware.timeout(process);
        } catch (RequestIO interrupt) {
            hardware.requestIO(process);
        }
    }

    private static List<Process> initialize(Hardware hardware) {
        var processes = new ArrayList<Process>();
        processes.add(new Process(hardware, "Process1", new CPUUsage(0, 0, 17), Order.HIGH, Priority.HIGH, ProcessType.CPU));
        processes.add(new Process(hardware, "Process2", new CPUUsage(5, 3, 10), Order.HIGH, Priority.LOW, ProcessType.IO));
        processes.add(new Process(hardware, "Process3", new CPUUsage(0, 0, 5), Order.LOW, Priority.MEDIUM, ProcessType.CPU));
        processes.add(new Process(hardware, "Process4", new CPUUsage(7, 8, 14), Order.MEDIUM, Priority.HIGH, ProcessType.IO));
        processes.add(new Process(hardware, "Process5", new CPUUsage(3, 6, 9), Order.HIGH, Priority.MEDIUM, ProcessType.IO));
        return processes;
    }

    private static void showState(Process process) {
        System.out.println("---------");
        System.out.printf("Process executing: %s%n", process.getName());
        System.out.printf("Remaining credits: %s%n", process.getCredits());
        System.out.printf("Total CPU time: %s%n", process.getTotalCPUTime());
        System.out.printf("State: %s%n", process.getState());
        System.out.println("---------");
    }
}