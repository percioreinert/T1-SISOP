import interrupt.FinishProgram;
import interrupt.Hardware;
import interrupt.RequestIO;
import os.OperationalSystem;
import os.Scheduler;
import process.Process;
import process.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        var operationalSystem = init();

        Process scheduledProcess;
        while (true) {
            try {
                operationalSystem.verifyState();
                scheduledProcess = operationalSystem.schedule();
                operationalSystem.compute(scheduledProcess);
                operationalSystem.processIO();
            } catch (IndexOutOfBoundsException e) {
                operationalSystem.insertCredits();
            } catch (FinishProgram e) {
                exit(0);
            }
        }
    }

    private static OperationalSystem init() {
        var scheduler = new Scheduler();
        var operationalSystem = new OperationalSystem(scheduler);
        var hardware = new Hardware(operationalSystem);
        operationalSystem.setHardware(hardware);
        var processes = initialize(operationalSystem);
        operationalSystem.admitProcesses(processes);

        return operationalSystem;
    }

    private static List<Process> initialize(OperationalSystem os) {
        var processes = new ArrayList<Process>();
        processes.add(new Process(os, "Process1", new CPUUsage(0, 0, 17), Order.HIGH, Priority.HIGH, ProcessType.CPU));
        processes.add(new Process(os, "Process2", new CPUUsage(5, 3, 10), Order.HIGH, Priority.LOW, ProcessType.IO));
        processes.add(new Process(os, "Process3", new CPUUsage(0, 0, 5), Order.LOW, Priority.MEDIUM, ProcessType.CPU));
        processes.add(new Process(os, "Process4", new CPUUsage(7, 8, 14), Order.MEDIUM, Priority.HIGH, ProcessType.IO));
        processes.add(new Process(os, "Process5", new CPUUsage(3, 6, 9), Order.HIGH, Priority.MEDIUM, ProcessType.IO));
        return processes;
    }
}