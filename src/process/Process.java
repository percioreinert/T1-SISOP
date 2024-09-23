package process;

import interrupt.Hardware;

public class Process {

    private final Hardware hardware;
    private final String name;
    private final CPUUsage cpuUsage;
    private final ProcessType type;
    private final Order order;
    private Priority priority;
    private State state;
    private int credits;
    private int IOCounter;
    private int cpuTimeCounter = 0;
    private int burstTimeCounter = 0;
    private int totalCPUTime = 0;

    public Process(Hardware hardware, String name, CPUUsage cpuUsage, Order order, Priority priority, ProcessType type) {
        this.hardware = hardware;
        this.name = name;
        this.cpuUsage = cpuUsage;
        this.order = order;
        this.priority = priority;
        this.state = State.READY;
        this.IOCounter = cpuUsage.IOTime();
        this.type = type;
        this.credits = creditsByPriority();
    }

    public String getName() {
        return name;
    }

    public Order getOrder() {
        return order;
    }

    public Priority getPriority() {
        return priority;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getCredits() {
        return this.credits;
    }

    public void consumeCredit() {
        this.credits -= 1;
        if (this.credits == 0) {
            this.hardware.block(this);
        }
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int creditsByPriority() {
        return switch (this.priority) {
            case LOWEST -> 1;
            case LOW -> 2;
            case MEDIUM -> 3;
            case HIGH -> 4;
            case HIGHEST -> 5;
        };
    }

    public void awaitIO() {
        this.IOCounter -= 1;
        if (IOCounter == 0) {
            this.hardware.responseIO(this);
            this.IOCounter = this.cpuUsage.IOTime();
        }
    }

    public void consumeCPUTime() {
        switch (this.type) {
            case CPU -> consumeTotalCPUTime();
            case IO -> consumeBurstTime();
        }
    }

    public void consumeTotalCPUTime() {
        this.cpuTimeCounter += 1;
        this.totalCPUTime += 1;
        if (this.cpuTimeCounter == cpuUsage.totalCPUTime()) {
            this.hardware.unschedule(this, this.type);
        }
    }

    public void consumeBurstTime() {
        this.burstTimeCounter += 1;
        this.cpuTimeCounter += 1;
        this.totalCPUTime += 1;
        if (cpuTimeCounter == cpuUsage.totalCPUTime()) {
            this.hardware.unschedule(this, ProcessType.CPU);
        } else if (this.burstTimeCounter == cpuUsage.burst()) {
            this.burstTimeCounter = 0;
            this.hardware.unschedule(this, this.type);
        }
    }

    public void lowerPriority() {
        this.priority = switch (priority) {
            case HIGHEST -> Priority.HIGH;
            case HIGH -> Priority.MEDIUM;
            case MEDIUM -> Priority.LOW;
            case LOW, LOWEST -> Priority.LOWEST;
        };
    }

    public int getTotalCPUTime() {
        return this.totalCPUTime;
    }
}
