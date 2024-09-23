package os;

import interrupt.Hardware;
import interrupt.RequestIO;
import process.Process;

import java.util.List;

public class OperationalSystem {

    private Hardware hardware;
    private final Scheduler scheduler;
    private List<Process> processes;

    public OperationalSystem(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public void admitProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }

    public Hardware getHardware() {
        return this.hardware;
    }

    public void verifyState() {
        this.scheduler.verifyState(this.processes);
    }

    public Process schedule() {
        return this.scheduler.schedule(this.processes);
    }

    public void processIO() {
        this.hardware.processIO(this.processes);
    }

    public void compute(Process process) throws InterruptedException {
        Thread.sleep(1);
        try {
            process.consumeCredit();
            process.consumeCPUTime();
            process.lowerPriority();
            showState(process);
            this.hardware.timeout(process);
        } catch (RequestIO interrupt) {
            this.hardware.requestIO(process);
        }
    }

    private static void showState(Process process) {
        System.out.println("---------");
        System.out.printf("Process executing: %s%n", process.getName());
        System.out.printf("Remaining credits: %s%n", process.getCredits());
        System.out.printf("Total CPU time: %s%n", process.getTotalCPUTime());
        System.out.printf("State: %s%n", process.getState());
        System.out.println("---------");
    }

    public void insertCredits() {
        this.scheduler.insertCredits(this.processes);
    }
}
