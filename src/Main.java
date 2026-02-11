import java.io.*;
import java.util.Locale;
import java.util.ArrayList;

/**
 * Main entry point for GigMatch Pro platform.
 */
public class Main {
    
    public static Service[] Services = new Service[] {User.paint, User.web_dev, User.graphic_design, User.data_entry, User.tutoring, User.cleaning, User.writing, User.photography, User.plumbing, User.electrical};
    public static HashTable<String, User> userTable = new HashTable<>(1009);
    public static ArrayList<Object[]> changeServiceType = new ArrayList<>();                // queued service type changes in month
    public static ArrayList<Freelancer> lastBurnout = new ArrayList<>();                    // last month's burntout people
    public static void addMonthQueue(Freelancer freeL, Service service, int price) {        // service type change adder
        Object[] instruction = new Object[]{freeL, service, price};
        changeServiceType.add(instruction);
    }
    public static void popMonthQueue() {                                                    // service type change executer at the end of the month
        for (int i = 0; i < changeServiceType.size(); i++) {
            Object[] instruction = changeServiceType.get(i);
            Freelancer freeL = (Freelancer) instruction[0];
            if (userTable.containsKey(freeL.id)){
                Service service = (Service) instruction[1];
                int price = (Integer) instruction[2];
                freeL.service.heap.remove(freeL);
                freeL.service = service;
                freeL.price = price;
                freeL.calculateCompositeScore();
                freeL.service.heap.insert(freeL);
            }
        }
        changeServiceType.clear();
    }

    public static void mcountNull() {           // turn every mcompleted and mcancelled to 0 at the end of the month
        for (Service s : Services) {
            s.heap.cleanMonth();
        }
        }

    public static boolean validService(String s) {          // valid service name
        if (s.equals("paint") || s.equals("web_dev") || s.equals("graphic_design") || s.equals("data_entry") || s.equals("tutoring") || s.equals("cleaning") || s.equals("writing") || s.equals("photography") || s.equals("plumbing") || s.equals("electrical")) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 2) {
            System.err.println("Usage: java Main <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                processCommand(line, writer);
            }

        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processCommand(String command, BufferedWriter writer)
            throws IOException {

        String[] parts = command.split("\\s+");
        String operation = parts[0];

        try {
            String result = "";

            switch (operation) {
                case "register_customer":
                    // Format: register_customer customerID
                    Customer customer = new Customer(parts[1]);
                    if(!userTable.put(parts[1], customer)) {
                        result = "Some error occurred in register_customer.";
                        customer = null;
                    } else {
                        result = "registered customer " + parts[1];
                    }
                    break;

                case "register_freelancer":
                    // Format: register_freelancer freelancerID serviceName basePrice T C R E A
                    String id = parts[1];
                    String service = parts[2];
                    int price = Integer.parseInt(parts[3]);
                    int T = Integer.parseInt(parts[4]);
                    int C = Integer.parseInt(parts[5]);
                    int R = Integer.parseInt(parts[6]);
                    int E = Integer.parseInt(parts[7]);
                    int A = Integer.parseInt(parts[8]);
                    if(validService(service) && 0 <= price && 0 <= T && T <= 100 && 0 <= C && C <= 100 && 0 <= R && R <= 100 && 0 <= E && E <= 100 && 0 <= A && A <= 100) {
                        Freelancer freelancer = new Freelancer(id, service, price, T, C, R, E, A);
                        if (userTable.put(id, freelancer)) {
                        result += "registered freelancer " + id;
                        freelancer.service.heap.insert(freelancer); }
                        else {result = "Some error occured in register_freelancer."; freelancer = null;}
                    } else {
                        result = "Some error occurred in register_freelancer.";
                    }
                    break;

                case "request_job":
                    // Format: request_job customerID serviceName topK
                    if(userTable.containsKey(parts[1]) && userTable.get(parts[1]) instanceof Customer && validService(parts[2]) && 0 < Integer.parseInt(parts[3])) {
                        Customer cust = (Customer) userTable.get(parts[1]);
                        ArrayList<Freelancer> a = cust.hireBestFreelancer(parts[2], Integer.parseInt(parts[3]));
                        if (a.isEmpty()) {
                            result = "no freelancers available";
                        } else {
                            result = "available freelancers for " + parts[2] + " (top " + parts[3] + "):";
                            for (int i = 0; i < Integer.parseInt(parts[3]); i++) {
                                Freelancer b = a.get(i);
                                result += "\n" + b.id + " - composite: " + b.compositeScore + ", price: " + b.price + ", rating: " + String.format("%.1f", b.averageRating);
                            }
                            result += "\n" + "auto-employed best freelancer: " + a.get(0).id + " for customer " + cust.id ;
                            Freelancer freeL = a.get(0);
                            freeL.employed = true;
                            freeL.cust = cust;
                            cust.totalEmployed++;
                        }
                    } else {
                        result = "Some error occurred in request_job.";
                    }
                    break;

                case "employ_freelancer":
                    // Format: employ_freelancer customerID freelancerID
                    if(userTable.containsKey(parts[1]) && userTable.containsKey(parts[2]) && userTable.get(parts[1]) instanceof Customer && userTable.get(parts[2]) instanceof Freelancer) {
                        Customer cust = (Customer) userTable.get(parts[1]);
                        Freelancer freeL = (Freelancer) userTable.get(parts[2]);
                        if(!freeL.employed && !cust.blacklistedFreelancers.contains(freeL.id)) {
                            freeL.employed = true;
                            cust.totalEmployed += 1;
                            result = cust.id + " employed " + freeL.id + " for " + freeL.service.name;
                            freeL.cust = cust;
                        } else {
                            result = "Some error occurred in employ_freelancer.";
                        }
                    } else {
                        result = "Some error occurred in employ_freelancer.";
                    }
                        
                    break;

                case "complete_and_rate":
                    // Format: complete_and_rate freelancerID rating
                    if(userTable.containsKey(parts[1]) && userTable.get(parts[1]) instanceof Freelancer && 0 <= Integer.parseInt(parts[2]) && Integer.parseInt(parts[2]) <= 5) {
                        Freelancer freeL = (Freelancer) userTable.get(parts[1]);
                        if(freeL.employed) {
                            freeL.employed = false;
                            freeL.addRating(Integer.parseInt(parts[2]));
                            result = freeL.id + " completed job for " + freeL.cust.id + " with rating " + parts[2];
                            freeL.cust.totalSpent += (int) (freeL.price * (1 - freeL.cust.subsidy));
                            if (freeL.mcompleted >= 5) {}
                            if (Integer.parseInt(parts[2]) >= 4) {
                                freeL.applySkillGains();
                            }
                            freeL.service.heap.remove(freeL);
                            freeL.calculateCompositeScore();
                            freeL.service.heap.insert(freeL);
                        } else {
                            result = "Some error occurred in complete_and_rate.";
                        }
                    } else {
                        result = "Some error occurred in complete_and_rate.";
                    }
                    break;

                case "cancel_by_freelancer":
                    // Format: cancel_by_freelancer freelancerID
                    if(userTable.containsKey(parts[1]) && userTable.get(parts[1]) instanceof Freelancer) {
                        Freelancer freeL = (Freelancer) userTable.get(parts[1]);
                        if(freeL.employed) {
                            freeL.employed = false;
                            freeL.addCancelPenalty();
                            result = "cancelled by freelancer: " + freeL.id + " cancelled " + freeL.cust.id;
                            freeL.service.heap.remove(freeL);
                            freeL.calculateCompositeScore();
                            freeL.service.heap.insert(freeL);
                            if (freeL.mcancelled >= 5) {
                                freeL.service.heap.remove(freeL);
                                userTable.remove(freeL.id);
                                result += "\n" + "platform banned freelancer: " + freeL.id;
                            }
                        } else {
                            result = "Some error occurred in cancel_by_freelancer.";
                        }
                    } else {
                        result = "Some error occurred in cancel_by_freelancer.";
                    }
                    break;

                case "cancel_by_customer":
                    // Format: cancel_by_customer customerID freelancerID
                    if (userTable.containsKey(parts[1]) && userTable.containsKey(parts[2]) && userTable.get(parts[1]) instanceof Customer && userTable.get(parts[2]) instanceof Freelancer) {
                        Customer cust = (Customer) userTable.get(parts[1]);
                        Freelancer freeL = (Freelancer) userTable.get(parts[2]);
                        if(freeL.employed) {
                            freeL.employed = false;
                            cust.cancelled += 1;
                            result = "cancelled by customer: " + cust.id + " cancelled " + freeL.id;
                            freeL.service.heap.insert(freeL);
                        } else {
                            result = "Some error occurred in cancel_by_customer.";
                        }
                    } else {
                        result = "Some error occurred in cancel_by_customer.";
                    }
                    break;

                case "blacklist":
                    // Format: blacklist customerID freelancerID
                    if(userTable.containsKey(parts[1]) && userTable.containsKey(parts[2]) && userTable.get(parts[1]) instanceof Customer && userTable.get(parts[2]) instanceof Freelancer) {
                        Customer cust = (Customer) userTable.get(parts[1]);
                        Freelancer freeL = (Freelancer) userTable.get(parts[2]);
                        if(!cust.blacklistedFreelancers.contains(freeL.id)) {
                            cust.blacklistedFreelancers.add(freeL.id);
                            cust.totalBlacklisted += 1;
                            result = cust.id + " blacklisted " + freeL.id;
                        } else {
                            result = "Some error occurred in blacklist.";
                        }
                    } else {
                        result = "Some error occurred in blacklist.";
                    }
                    break;

                case "unblacklist":
                    // Format: unblacklist customerID freelancerID
                    if(userTable.containsKey(parts[1]) && userTable.containsKey(parts[2]) && userTable.get(parts[1]) instanceof Customer && userTable.get(parts[2]) instanceof Freelancer) {
                        Customer cust = (Customer) userTable.get(parts[1]);
                        Freelancer freeL = (Freelancer) userTable.get(parts[2]);
                        if(cust.blacklistedFreelancers.contains(freeL.id)) {
                            cust.blacklistedFreelancers.remove(freeL.id);
                            cust.totalBlacklisted -= 1;
                            result = cust.id + " unblacklisted " + freeL.id;
                        } else {
                            result = "Some error occurred in unblacklist.";
                        }
                    } else {
                        result = "Some error occurred in unblacklist.";
                    }
                    break;

                case "change_service":
                    // Format: change_service freelancerID newService newPrice
                    if (userTable.containsKey(parts[1]) && userTable.get(parts[1]) instanceof Freelancer && validService(parts[2]) && 0 <= Integer.parseInt(parts[3])) {
                        Freelancer freeL = (Freelancer) userTable.get(parts[1]);
                        result = "service change for " + freeL.id + " queued from " + freeL.service.name + " to";
                        Service serviceof = freeL.searchService(parts[2]);
                        result += " " + parts[2];
                        int newprice = Integer.parseInt(parts[3]);
                        addMonthQueue(freeL, serviceof, newprice);
                    } else {
                        result = "Some error occured in change_service." ;
                    }
                    break;

                case "simulate_month":
                    // Format: simulate_month
                    ArrayList<Freelancer> temp = new ArrayList<>();
                    for (Object obj : ((HashTable) userTable).values) {
                            if (obj instanceof Freelancer freeL) {
                                if (lastBurnout.contains(freeL) && freeL.mcompleted <= 2 ) {
                                    freeL.burnout = false;
                                    freeL.service.heap.remove(freeL);
                                    freeL.calculateCompositeScore();
                                    freeL.service.heap.insert(freeL);
                                } else if (freeL.mcompleted >= 5) {
                                    freeL.burnout = true;
                                    freeL.service.heap.remove(freeL);
                                    freeL.calculateCompositeScore();
                                    freeL.service.heap.insert(freeL);
                                    temp.add(freeL);
                                }
                                freeL.mcancelled = 0;
                                freeL.mcompleted = 0;
                            } else if (obj instanceof Customer cust) {
                                cust.updateLoyaltyTier();
                            }
                    }
                    lastBurnout.clear();
                    lastBurnout = temp;
                    popMonthQueue();
                    result = "month complete" ;
                    break;

                case "query_freelancer":
                    // Format: query_freelancer freelancerID
                    if (userTable.containsKey(parts[1]) && userTable.get(parts[1]) instanceof Freelancer) {
                        Freelancer freeL = (Freelancer) userTable.get(parts[1]);
                        result = freeL.id + ": " + freeL.service.name + ", price: " + freeL.price + ", rating: " + String.format("%.1f", freeL.averageRating) + ", completed: " + freeL.ncompleted + ", cancelled: " + freeL.ncancelled + ", skills: (" + freeL.skills[0] + "," + freeL.skills[1] + "," + freeL.skills[2] + "," + freeL.skills[3] + "," + freeL.skills[4] +"), available: " + (freeL.employed ? "no" : "yes")+ ", burnout: " + (freeL.burnout ? "yes" : "no");
                    } else {
                        result = "Some error occurred in query_freelancer.";
                    }
                    break;

                case "query_customer":
                    // Format: query_customer customerID
                    if (userTable.containsKey(parts[1]) && userTable.get(parts[1]) instanceof Customer) {
                        Customer cust = (Customer) userTable.get(parts[1]);
                        result = cust.id + ": total spent: $" + cust.totalSpent + ", loyalty tier: " + cust.loyaltyTier + ", blacklisted freelancer count: " + cust.totalBlacklisted + ", total employment count: " + cust.totalEmployed;
                    } else {
                        result = "Some error occurred in query_customer.";
                    }
                    break;

                case "update_skill":
                    // Format: update_skill freelancerID T C R E A
                    int newT = Integer.parseInt(parts[2]);
                    int newC = Integer.parseInt(parts[3]);
                    int newR = Integer.parseInt(parts[4]);
                    int newE = Integer.parseInt(parts[5]);
                    int newA = Integer.parseInt(parts[6]);
                    if (userTable.containsKey(parts[1]) && userTable.get(parts[1]) instanceof Freelancer && 0 <= newT && 0 <= newC && 0 <= newR && 0 <= newE && 0 <= newA && newT <= 100 && newC <= 100 && newR <= 100 && newE <= 100 && newA <= 100) {
                        Freelancer freeL = (Freelancer) userTable.get(parts[1]);
                        freeL.skills[0] = newT;
                        freeL.skills[1] = newC;
                        freeL.skills[2] = newR;
                        freeL.skills[3] = newE;
                        freeL.skills[4] = newA;
                        freeL.service.heap.remove(freeL);
                        freeL.calculateCompositeScore();
                        freeL.service.heap.insert(freeL);
                        result = "updated skills of " + freeL.id + " for " + freeL.service.name;
                    } 
                    break;

                default:
                    result = "Unknown command: " + operation;
            }

            writer.write(result);
            writer.newLine();

        } catch (Exception e) {
            writer.write("Error processing command: " + command + " (" + e + ")");
            writer.newLine();
        }
    }
}