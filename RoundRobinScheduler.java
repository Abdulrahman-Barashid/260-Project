import java.util.*;

class Process {
    String name;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int waitingTime;
    int turnaroundTime;
    int responseTime;

    Process(String name, int arrivalTime, int burstTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.responseTime = -1; // Default response time, will be updated during execution
    }
}

public class RoundRobinScheduler {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = scanner.nextInt();
        double pCounter = 0.0;
        System.out.println("Enter process name, arrival time, and burst time for each process:");
        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            pCounter++;
            System.out.print("Process " + (i + 1) + " Name: ");
            String name = scanner.next();
            System.out.print("Process " + (i + 1) + " Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Process " + (i + 1) + " Burst Time: ");
            int burstTime = scanner.nextInt();
            processes.add(new Process(name, arrivalTime, burstTime));
        }
        System.out.print("Enter time quantum: ");
        int quantum = scanner.nextInt();
        System.out.println("-------------------------------------Processes details---------------------------------------------");
        System.out.println("---------------------------------------------------------------------------------------------------");
        System.out.println("Process\t Arrival Time\t Burst Time\t Turnaround Time\t Waiting Time\t Response Time");
        System.out.println("---------------------------------------------------------------------------------------------------");

        roundRobin(processes, quantum, pCounter);

        scanner.close();
    }

    public static void roundRobin(List<Process> processes, int quantum, double pCounter) {
        Queue<Process> readyQueue = new LinkedList<>();
        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int totalResponseTime = 0;

        Process runningProcess = null;

        while (!processes.isEmpty() || runningProcess != null || !readyQueue.isEmpty()) {
            // Adding arrived processes to the ready queue
            for (Iterator<Process> iter = processes.iterator(); iter.hasNext();) {
                Process p = iter.next();
                if (p.arrivalTime <= currentTime) {
                    readyQueue.offer(p);
                    iter.remove();
                }
            }

            // If there's no process running and ready queue is not empty, start executing a
            // process
            if (runningProcess == null && !readyQueue.isEmpty()) {
                runningProcess = readyQueue.poll();
                if (runningProcess.responseTime == -1) {
                    runningProcess.responseTime = currentTime - runningProcess.arrivalTime;
                    totalResponseTime += runningProcess.responseTime;
                }
            }

            // Execute the current process
            if (runningProcess != null) {
                int timeSlice = Math.min(quantum, runningProcess.remainingTime);
                currentTime += timeSlice;
                runningProcess.remainingTime -= timeSlice;

                // Check if more processes have arrived while this process was running
                for (Iterator<Process> iter = processes.iterator(); iter.hasNext();) {
                    Process p = iter.next();
                    if (p.arrivalTime <= currentTime && p != runningProcess) {
                        readyQueue.offer(p);
                        iter.remove();
                    }
                }

                // If the current process is not finished, put it back in the queue
                if (runningProcess.remainingTime > 0) {
                    readyQueue.offer(runningProcess);
                    runningProcess = null; // No process is running now
                } else {
                    // Process has finished
                    runningProcess.turnaroundTime = currentTime - runningProcess.arrivalTime;
                    runningProcess.waitingTime = runningProcess.turnaroundTime - runningProcess.burstTime;
                    totalTurnaroundTime += runningProcess.turnaroundTime;
                    totalWaitingTime += runningProcess.waitingTime;
                    System.out.println(runningProcess.name+"\t\t"+runningProcess.arrivalTime+"\t\t"+runningProcess.burstTime+"\t\t"+runningProcess.turnaroundTime
                    +"\t\t    "+runningProcess.waitingTime+"\t\t    "+runningProcess.responseTime);                    
                    runningProcess = null; // No process is running now
                }
            } else {
                // If no process is in the ready queue and no process is running, move time
                // forward
                currentTime++;
            }
        }

        // Calculate average turnaround, waiting, and response time
        double avgTurnaroundTime = (double) totalTurnaroundTime / pCounter;
        double avgWaitingTime = (double) totalWaitingTime / pCounter;
        double avgResponseTime = (double) totalResponseTime / pCounter;

        // Print results
        System.out
                .println("--------------------------------------------------------------------------------------------------");
        System.out.println("\nAverage Turnaround Time: " + avgTurnaroundTime);
        System.out.println("Average Waiting Time: " + avgWaitingTime);
        System.out.println("Average Response Time: " + avgResponseTime);

    }
}
