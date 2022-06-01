import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

public class Application {

    private static Family family = new Family();

    public static void main(String args[]) {

        if(args.length == 0) {
            System.out.println("Please provide file name as input.");
            return;
        }

        String fileName = args[0];
        boolean readFile = false;

        if (fileName != null) {
            // Parses the entire input file and returns true when done
            // Creating a family tree
            readFile = parseInputFile(fileName);
        }
        if (readFile) {
            try {
                Scanner sc = new Scanner(System.in);
                String choice;
                boolean exit = false;
                while (!exit) {
                    System.out.println("1. Add child");
                    System.out.println("2. Get mother with most daughters");
                    System.out.println("3. Get relation");
                    System.out.println("4. Exit");
                    System.out.print("Enter Choice: ");
                    choice = sc.nextLine();

                    switch (choice) {
                        case "1":
                            addChildInput();
                            break;
                        case "2":
                            motherWithMostDaughtersInput();
                            break;
                        case "3":
                            getRelationInput();
                            break;
                        case "4":
                            exit = true;
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter again.");
                            System.out.println();
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Unable to process input.");
            }
        }
    }

    private static void motherWithMostDaughtersInput() {
        // Gets the mother with most daughters
        List<Person> mothersWithMostDaughters = family.getMotherWithMostDaughters();
        if (mothersWithMostDaughters.size() == 0){
            System.out.println("There are no mothers with daughters.");
        }
        for (Person mother : mothersWithMostDaughters) {
            System.out.println(mother.getName());
        }
        System.out.println();
    }

    private static void addChildInput() {
        Person parent = null;
        String childName = "";
        Gender childGender = null;
        Scanner sc = new Scanner(System.in);

        while (true) {
            // Gets the name of the parent
            System.out.print("Parent: ");
            String person = sc.nextLine();
            parent = family.getMember(person);
            if (parent == null) {
                System.out.println("Person does not exist in the family. Going back to main menu.");
                System.out.println();
                return;
            } else {
                break;
            }
        }

        while (true) {
            // Gets the name of the child
            System.out.print("Child Name: ");
            childName = sc.nextLine();
            if (childName == "") {
                System.out.println("Please enter a name. Going back to main menu");
                System.out.println();
                return;
            } else {
                break;
            }
        }

        while (true) {
            // Gets the gender of the child
            System.out.print("Child Gender (M/F): ");
            String gender = sc.nextLine();
            if (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F")) {
                System.out.println("Invalid input. Going back to main menu.");
                System.out.println();
                return;
            } else {
                if (gender.trim().equalsIgnoreCase("M")) {
                    childGender = Gender.MALE;
                } else if (gender.trim().equalsIgnoreCase("F")) {
                    childGender = Gender.FEMALE;
                }
                break;
            }
        }

        // Adds child to family
        Person child = new Person(childName, childGender);
        family.addChild(parent, child);
        System.out.println("Child has been added to the family.");
        System.out.println();
    }

    private static void getRelationInput() {
        Person source = null;
        Person dest = null;
        Scanner sc = new Scanner(System.in);

        while (true) {
            // Gets the name of the first person
            System.out.print("Person1: ");
            String person = sc.nextLine();
            source = family.getMember(person);
            if (source == null) {
                System.out.println("Person does not exist in the family. Going back to main menu.");
                System.out.println();
                return;
            } else {
                break;
            }
        }

        while (true) {
            // Gets the name of the second person
            System.out.print("Person2: ");
            String relation = sc.nextLine();
            dest = family.getMember(relation);
            if (dest == null) {
                System.out.println("Relation does not exist in the family. Going back to main menu.");
                System.out.println();
                return;
            } else {
                break;
            }
        }

        // Gets the shortest relation between the two people
        System.out.println(family.getShortestRelation(source, dest));
        System.out.println();
    }

    private static boolean parseInputFile(String fileName) {
        try {
            File inputFile = new File(fileName);

            //parse the input file
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                String personPrefix = "PERSON:";
                String childPrefix = "CHILD:";
                String marriagePrefix = "SPOUSE:";

                if (line.startsWith(personPrefix)) {
                    // adds member to the family
                    line = line.substring(personPrefix.length());
                    String[] tokens = line.split(",");
                    Gender gender = null;
                    if (tokens[1].trim().equalsIgnoreCase("male")) {
                        gender = Gender.MALE;
                    } else if (tokens[1].trim().equalsIgnoreCase("female")) {
                        gender = Gender.FEMALE;
                    }
                    Person p = new Person(tokens[0].trim(), gender);
                    family.addMember(p);
                } else if (line.startsWith(childPrefix)) {
                    // adds member to family with parent connection
                    line = line.substring(childPrefix.length());
                    String[] tokens = line.split(",");
                    Person parent = family.getMember(tokens[0].trim());
                    Person child = family.getMember(tokens[1].trim());
                    family.addChild(parent, child);

                } else if (line.startsWith(marriagePrefix)) {
                    // adds member to family with marriage connection
                    line = line.substring(marriagePrefix.length());
                    String[] tokens = line.split(",");
                    Person person1 = family.getMember(tokens[0].trim());
                    Person person2 = family.getMember(tokens[1].trim());
                    family.addMarriage(person1, person2);
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println("Unable to read the file.");
            return false;
        }
    }
}
