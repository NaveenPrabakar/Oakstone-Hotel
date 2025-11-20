package Main;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import Main.Booking.Booking;
import Main.Booking.Reservation;
import Main.Booking.ReviewProcess;
import Main.Controller.*;
import Main.Data.DataRepository;
import Main.Data.FileDataRepository;
import Main.Employee.*;
import Main.Guest.*;
import Main.Room.room;

public class Main {
    public static String HOTEL_PATH = "";

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        Random random = new Random();
        int randomBannerNumber  = random.nextInt(4) + 1;
        switch (randomBannerNumber) {
            case 1:
                printBanner1();
                break;
            case 2:
                printBanner2();
                break;
            case 3:
                printBanner3();
                break;
            case 4:
                printBanner4();
                break;
        }
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("====================================");
            System.out.println("  Welcome to the HOTEL OAK STONE");
            System.out.println("====================================");
            System.out.print("Are you a Guest or a Worker? (G/W) or Q to quit: ");
            String userType = sc.nextLine().trim().toUpperCase();

            if (userType.equals("G")) {
                runGuestPortal(sc);
            }
            else if (userType.equals("W")) {
                System.out.print("Enter the Hotel Address you're working at: ");
                String hotelAddress = sc.nextLine().trim();
                HOTEL_PATH = hotelAddress.isEmpty() ? "123 Main St, Iowa" : hotelAddress;

                // Initialize repository for this hotel
                DataRepository repo = new FileDataRepository(Paths.get(HOTEL_PATH));
                EmployeeLoginService.initialize(repo);

                boolean workerMenu = true;

                while (workerMenu) {
                    System.out.println("\n===== WORKER ACCESS =====");
                    System.out.println("1. Login");
                    System.out.println("0. Back");
                    System.out.print("Select: ");

                    String choice = sc.nextLine().trim();

                    if (choice.equals("1")) {
                        Employee worker = WorkerLoginController.loginWorker(sc);
                        if (worker != null) {
                            initializeEmployeeManagers();
                            runWorkerPortal(sc, HOTEL_PATH, worker);
                        }
                    }
                    else if (choice.equals("0")) {
                        workerMenu = false;
                    }
                    else {
                        System.out.println("Invalid option.");
                    }
                }
            }

            else if (userType.equals("Q")) {
                System.out.println("Exiting system... Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid selection. Please choose Guest (G), Worker (W), or Quit (Q).");
            }
        }

        sc.close();

    }

    private static void initializeEmployeeManagers() throws FileNotFoundException {
        File file = new File(Main.HOTEL_PATH + "/" + "Employee.txt");
        Scanner scnr = new Scanner(file);

        frontdeskteam FDManager = null;
        Housekeeping HKManager = null;

        while (scnr.hasNextLine()) {
            String line = scnr.nextLine();
            String[] e = line.split(" ");
            if (e.length < 4) continue;

            int id = Integer.parseInt(e[0]);

            // role is always third-from-last
            String role = e[e.length - 3];

            // reconstruct name from tokens between ID and role
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 1; i < e.length - 3; i++) {
                nameBuilder.append(e[i]);
                if (i < e.length - 4) nameBuilder.append(" ");
            }

            String name = nameBuilder.toString();

            if (role.equals("FrontDesk")) {
                FDManager = new frontdeskteam(id, name);
            } else if (role.equals("Cleaner")) {
                HKManager = new Housekeeping(id, name);
            }
        }

        if (FDManager != null && HKManager != null) {
            FDManager.addHouseKeepingManager(HKManager);
        } else {
            System.out.println("WARNING: Could not link managers.");
        }
    }

    private static void runGuestPortal(Scanner sc) throws FileNotFoundException, InterruptedException {
        boolean running = true;

        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║          GUEST PORTAL              ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║  1. Book a Room                    ║");
            System.out.println("║  2. Check In                       ║");
            System.out.println("║  3. Check Out                      ║");
            System.out.println("║  4. Access Room (Key Card)         ║");
            System.out.println("║  5. Leave a review                 ║");
            System.out.println("║  0. Exit                           ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Select an option: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                Booking.handleBooking();
            } else if (choice.equals("2")) {
                System.out.println("Check In option selected.");
                CheckSystemController.RunCheckin(null);
            } else if (choice.equals("3")) {
                System.out.println("Check Out option selected.");
                CheckSystemController.RunCheckOut(null);
            } else if (choice.equals("4")) {
                handleRoomAccess(sc);
            } else if(choice.equals("5")){
                leaveReview(sc);
            } else if (choice.equals("0")) {
                System.out.println("Thank you for visiting our hotel. Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid option. Please choose 0–4.");
            }
        }
    }

    // ============================================================
    // Worker Portal (Employees, Data Team, Executives, etc.)
    // ============================================================

    private static void runWorkerPortal(Scanner sc, String hotelAddress, Employee loggedIn) throws FileNotFoundException, InterruptedException {
        System.out.println("Logged in as: " + loggedIn.getName() + " (" + loggedIn.role() + ")");

        boolean running = true;

        while (running) {
            printWorkerMenu(loggedIn);
            System.out.print("\nSelect an option: ");
            String choice = sc.nextLine().trim();
            String role = loggedIn.role();

            // Logout
            if (choice.equals("0")) {
                System.out.println("Logging out..." + loggedIn.getName() + " from " + loggedIn.role() + " Team");
                running = false;
                loggedIn = null;
                continue;
            } else if(choice.equals("9")){
                EmployeePortal portal = new EmployeePortal(Main.HOTEL_PATH + "/ClockData.txt");
                portal.runPortal(sc, loggedIn);
                continue; // <-- go back to Worker Portal menu immediately
            }

            // Role-based handling
            switch (role) {
                case "FrontDesk":
                    if (choice.equals("1")) {
                        frontdeskteam.processQueue(loggedIn.getName());
                    } else {
                        System.out.println("Invalid option for your role.");
                    }
                    break;

                case "CleaningStaff":
                    if (choice.equals("1")) {
                        Housekeeping.processCleaning(loggedIn.getName());
                    } else {
                        System.out.println("Invalid option for your role.");
                    }
                    break;

                case "Kitchen":
                    if (choice.equals("1")) {
                        KitchenPanel.processOrders(loggedIn.getName());
                    } else {
                        System.out.println("Invalid option for your role.");
                    }
                    break;

                case "DataTeam":
                    if (choice.equals("1")) {
                        DataTeamController.runDataAnalysis(loggedIn.getName());
                    } else {
                        System.out.println("Invalid option for your role.");
                    }
                    break;

                case "Executive":
                    if (choice.equals("1")) {
                        frontdeskteam.processQueue(loggedIn.getName());
                    } else if (choice.equals("2")) {
                        Housekeeping.processCleaning(loggedIn.getName());
                    } else if (choice.equals("3")) {
                        KitchenPanel.processOrders(loggedIn.getName());
                    } else if (choice.equals("4")) {
                        ExecutiveController.runExecutivePanel(loggedIn.getName());
                    } else {
                        System.out.println("Invalid option for your role.");
                    }
                    break;
                case "HR":
                    if (choice.equals("1")) {
                        HRController.runHRPanel(loggedIn.getName());
                    }
                    else if (choice.equals("2")) {
                        WorkerLoginController.registerNewWorker(sc);
                    }
                    else {
                        System.out.println("Invalid option for your role.");
                    }
                    break;

                default:
                    System.out.println("Unknown role.");
            }
        }
    }

    private static void printWorkerMenu(Employee user) {
        String role = user.role();

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║          WORKER PORTAL                 ║");
        System.out.println("╠════════════════════════════════════════╣");

        switch (role) {
            case "FrontDesk":
                System.out.println("║  1. Front Desk Panel                   ║");
                break;
            case "CleaningStaff":
                System.out.println("║  1. Cleaning Panel                     ║");
                break;
            case "Kitchen":
                System.out.println("║  1. Kitchen Panel                      ║");
                break;
            case "DataTeam":
                System.out.println("║  1. Data Team Panel                    ║");
                break;
            case "Executive":
                System.out.println("║  1. Front Desk Panel                   ║");
                System.out.println("║  2. Cleaning Panel                     ║");
                System.out.println("║  3. Kitchen Panel                      ║");
                System.out.println("║  4. Executive Panel                    ║");
                break;
            case "HR":
                System.out.println("║  1. HR Panel                           ║");
                System.out.println("║  2. Register New Worker                ║");
                break;
        }

        System.out.println("║  0. Logout                             ║");
        System.out.println("║  9. Clock In/Out                       ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void handleRoomAccess(Scanner sc) {
        System.out.print("Enter the hotel location you're accessing (e.g., Chicago, DesMoines): ");
        String hotel = sc.nextLine().trim();

        if (hotel.isEmpty()) {
            System.out.println("❌ No location entered. Returning to menu.");
            return;
        }

        Main.HOTEL_PATH = hotel;
        new RoomAccessFlow(sc).execute();
    }

    private static void leaveReview(Scanner sc){
        System.out.print("Enter the hotel location you're accessing (e.g., Chicago, DesMoines): ");
        String hotel = sc.nextLine().trim();

        if (hotel.isEmpty()) {
            System.out.println("No location entered. Returning to menu.");
            return;
        }

        Main.HOTEL_PATH = hotel;

        System.out.println("What's your name?");
        String name = sc.nextLine().trim();
        System.out.println("What's your reservation ID?");
        String resId = sc.nextLine().trim();
        Reservation reservation = new Reservation(name, resId);
        if (reservation.getStatus()) {
            System.out.println("I have located your booking! You stayed with us until " + reservation.getEndDate());
            ReviewProcess reviewProcess = new ReviewProcess(reservation);
            reviewProcess.execute(sc);
        }
    }

    private static void printBanner1() {
        System.out.println();
        System.out.println("     $$$$$$\\   $$$$$$\\  $$\\   $$\\  $$$$$$\\ $$$$$$$$\\  $$$$$$\\  $$\\   $$\\ $$$$$$$$\\");
        System.out.println("    $$  __$$\\ $$  __$$\\ $$ | $$  |$$  __$$\\\\__$$  __|$$  __$$\\ $$$\\  $$ |$$  _____|");
        System.out.println("    $$ /  $$ |$$ /  $$ |$$ |$$  / $$ /  \\__|  $$ |   $$ /  $$ |$$$$\\ $$ |$$ |      ");
        System.out.println("    $$ |  $$ |$$$$$$$$ |$$$$$  /  \\$$$$$$\\    $$ |   $$ |  $$ |$$ $$\\$$ |$$$$$\\    ");
        System.out.println("    $$ |  $$ |$$  __$$ |$$  $$<    \\____$$\\   $$ |   $$ |  $$ |$$ \\$$$$ |$$  __|");
        System.out.println("    $$ |  $$ |$$ |  $$ |$$ |\\$$\\  $$\\   $$ |  $$ |   $$ |  $$ |$$ |\\$$$ |$$ |");
        System.out.println("     $$$$$$  |$$ |  $$ |$$ | \\$$\\ \\$$$$$$  |  $$ |    $$$$$$  |$$ | \\$$ |$$$$$$$$\\");
        System.out.println("     \\______/ \\__|  \\__|\\__|  \\__| \\______/   \\__|    \\______/ \\__|  \\__|\\________|");
        System.out.println("\n                          O   A   K   S   T   O   N   E   \n                                H   O   T   E   L\n");
    }

    private static void printBanner2() {
        System.out.println();
        System.out.println("   ===================================================================================");
        System.out.println("   ===    =======  =====  ====  ===      ===        ====    ====  =======  ==        =");
        System.out.println("   ==  ==  =====    ====  ===  ===  ====  =====  ======  ==  ===   ======  ==  =======");
        System.out.println("   =  ====  ===  ==  ===  ==  ====  ====  =====  =====  ====  ==    =====  ==  =======");
        System.out.println("   =  ====  ==  ====  ==  =  ======  ==========  =====  ====  ==  ==  ===  ==  =======");
        System.out.println("   =  ====  ==  ====  ==     ========  ========  =====  ====  ==  ===  ==  ==      ===");
        System.out.println("   =  ====  ==        ==  ==  =========  ======  =====  ====  ==  ====  =  ==  =======");
        System.out.println("   =  ====  ==  ====  ==  ===  ===  ====  =====  =====  ====  ==  =====    ==  =======");
        System.out.println("   ==  ==  ===  ====  ==  ====  ==  ====  =====  ======  ==  ===  ======   ==  =======");
        System.out.println("   ===    ====  ====  ==  ====  ===      ======  =======    ====  =======  ==        =");
        System.out.println("   ===================================================================================");
        System.out.println("\n                          O   A   K   S   T   O   N   E   \n                                H   O   T   E   L\n");
    }

    private static void printBanner3() {
        System.out.println();
        System.out.println("   :'#######:::::'###::::'##:::'##::'######::'########::'#######::'##::: ##:'########:");
        System.out.println("   '##.... ##:::'## ##::: ##::'##::'##... ##:... ##..::'##.... ##: ###:: ##: ##.....::");
        System.out.println("    ##:::: ##::'##:. ##:: ##:'##::: ##:::..::::: ##:::: ##:::: ##: ####: ##: ##:::::::");
        System.out.println("    ##:::: ##:'##:::. ##: #####::::. ######::::: ##:::: ##:::: ##: ## ## ##: ######:::");
        System.out.println("    ##:::: ##: #########: ##. ##::::..... ##:::: ##:::: ##:::: ##: ##. ####: ##...::::");
        System.out.println("    ##:::: ##: ##.... ##: ##:. ##::'##::: ##:::: ##:::: ##:::: ##: ##:. ###: ##:::::::");
        System.out.println("   . #######:: ##:::: ##: ##::. ##:. ######::::: ##::::. #######:: ##::. ##: ########:");
        System.out.println("   :.......:::..:::::..::..::::..:::......::::::..::::::.......:::..::::..::..........::");
        System.out.println("\n                          O   A   K   S   T   O   N   E   \n                                H   O   T   E   L\n");
    }

    private static void printBanner4() {
        System.out.println();
        System.out.println("                                                                                                                                                                                     ");
        System.out.println("                                                                                                                                                                                     ");
        System.out.println("         OOOOOOOOO                 AAA               KKKKKKKKK    KKKKKKK   SSSSSSSSSSSSSSS TTTTTTTTTTTTTTTTTTTTTTT     OOOOOOOOO     NNNNNNNN        NNNNNNNNEEEEEEEEEEEEEEEEEEEEEE");
        System.out.println("       OO:::::::::OO              A:::A              K:::::::K    K:::::K SS:::::::::::::::ST:::::::::::::::::::::T   OO:::::::::OO   N:::::::N       N::::::NE::::::::::::::::::::E");
        System.out.println("     OO:::::::::::::OO           A:::::A             K:::::::K    K:::::KS:::::SSSSSS::::::ST:::::::::::::::::::::T OO:::::::::::::OO N::::::::N      N::::::NE::::::::::::::::::::E");
        System.out.println("    O:::::::OOO:::::::O         A:::::::A            K:::::::K   K::::::KS:::::S     SSSSSSST:::::TT:::::::TT:::::TO:::::::OOO:::::::ON:::::::::N     N::::::NEE::::::EEEEEEEEE::::E");
        System.out.println("    O::::::O   O::::::O        A:::::::::A           KK::::::K  K:::::KKKS:::::S            TTTTTT  T:::::T  TTTTTTO::::::O   O::::::ON::::::::::N    N::::::N  E:::::E       EEEEEE");
        System.out.println("    O:::::O     O:::::O       A:::::A:::::A            K:::::K K:::::K   S:::::S                    T:::::T        O:::::O     O:::::ON:::::::::::N   N::::::N  E:::::E             ");
        System.out.println("    O:::::O     O:::::O      A:::::A A:::::A           K::::::K:::::K     S::::SSSS                 T:::::T        O:::::O     O:::::ON:::::::N::::N  N::::::N  E::::::EEEEEEEEEE   ");
        System.out.println("    O:::::O     O:::::O     A:::::A   A:::::A          K:::::::::::K       SS::::::SSSSS            T:::::T        O:::::O     O:::::ON::::::N N::::N N::::::N  E:::::::::::::::E   ");
        System.out.println("    O:::::O     O:::::O    A:::::A     A:::::A         K:::::::::::K         SSS::::::::SS          T:::::T        O:::::O     O:::::ON::::::N  N::::N:::::::N  E:::::::::::::::E   ");
        System.out.println("    O:::::O     O:::::O   A:::::AAAAAAAAA:::::A        K::::::K:::::K           SSSSSS::::S         T:::::T        O:::::O     O:::::ON::::::N   N:::::::::::N  E::::::EEEEEEEEEE   ");
        System.out.println("    O:::::O     O:::::O  A:::::::::::::::::::::A       K:::::K K:::::K               S:::::S        T:::::T        O:::::O     O:::::ON::::::N    N::::::::::N  E:::::E             ");
        System.out.println("    O::::::O   O::::::O A:::::AAAAAAAAAAAAA:::::A    KK::::::K  K:::::KKK            S:::::S        T:::::T        O::::::O   O::::::ON::::::N     N:::::::::N  E:::::E       EEEEEE");
        System.out.println("    O:::::::OOO:::::::OA:::::A             A:::::A   K:::::::K   K::::::KSSSSSSS     S:::::S      TT:::::::TT      O:::::::OOO:::::::ON::::::N      N::::::::NEE::::::EEEEEEEE:::::E");
        System.out.println("     OO:::::::::::::OOA:::::A               A:::::A  K:::::::K    K:::::KS::::::SSSSSS:::::S      T:::::::::T       OO:::::::::::::OO N::::::N       N:::::::NE::::::::::::::::::::E");
        System.out.println("       OO:::::::::OO A:::::A                 A:::::A K:::::::K    K:::::KS:::::::::::::::SS       T:::::::::T         OO:::::::::OO   N::::::N        N::::::NE::::::::::::::::::::E");
        System.out.println("         OOOOOOOOO  AAAAAAA                   AAAAAAAKKKKKKKKK    KKKKKKK SSSSSSSSSSSSSSS         TTTTTTTTTTT           OOOOOOOOO     NNNNNNNN         NNNNNNNEEEEEEEEEEEEEEEEEEEEEE");
        System.out.println("                                                                                                                                                                                     ");
        System.out.println("                                                                                                                                                                                     ");
        System.out.println("\n                                                                              O   A   K   S   T   O   N   E   \n                                                                                     H   O   T   E   L\n");
    }


    private static final class RoomAccessFlow {
        private static final String DATA_ROOT = "Backend/Hotel/untitled/src/Main/";
        private static final String FALLBACK_ROOT = "src/Main/";
        private final Scanner input;

        RoomAccessFlow(Scanner input) {
            this.input = input;
        }

        void execute() {
            System.out.println("===== ROOM ACCESS AUTHENTICATION =====");

            List<KeyCard> issuedCards = loadIssuedKeyCards();

            if (issuedCards.isEmpty()) {
                System.out.println("No key cards are currently issued. Please visit the front desk.");
                return;
            }

            System.out.print("Room number: ");
            int roomNumber = parseNumber(input.nextLine().trim());

            System.out.print("Name on key card: ");
            String guestName = input.nextLine().trim();

            if (roomNumber <= 0 || guestName.isEmpty()) {
                System.out.println("Access denied: incomplete information.");
                return;
            }

            Optional<KeyCard> keyCard = findMatchingCard(issuedCards, roomNumber, guestName);

            if (keyCard.isEmpty()) {
                System.out.println(RoomAccessResult.INVALID_GUEST.getMessage());
                return;
            }

            Optional<room> targetRoom = loadRoom(roomNumber);

            if (targetRoom.isEmpty()) {
                System.out.println(RoomAccessResult.SYSTEM_ERROR.getMessage());
                return;
            }

            Guest guest = new Guest(guestName, -1);
            RoomAccessResult result = guest.scanKeyCard(keyCard.get(), targetRoom.get());

            System.out.println(result.getMessage());

            if (result == RoomAccessResult.ACCESS_GRANTED) {
                GuestSession.roomAccess(roomNumber, guestName, targetRoom.get());
            }
        }

        private List<KeyCard> loadIssuedKeyCards() {
            File source = resolveDataFile(Main.HOTEL_PATH +"/"+"Keycard.txt");
            List<KeyCard> cards = new ArrayList<>();

            if (!source.exists()) return cards;

            try (Scanner scanner = new Scanner(source)) {
                while (scanner.hasNextLine()) {
                    String[] tokens = scanner.nextLine().trim().split("\\s+");

                    if (tokens.length >= 3) {
                        int recordedRoom = parseNumber(tokens[0]);
                        boolean isActive = Boolean.parseBoolean(tokens[1]);
                        String ownerName = rebuildName(tokens);

                        cards.add(new KeyCard(recordedRoom, isActive, new Guest(ownerName, -1)));
                    }
                }
            } catch (FileNotFoundException e) {
                cards.clear();
            }

            return cards;
        }

        private Optional<KeyCard> findMatchingCard(List<KeyCard> cards, int roomNumber, String guestName) {
            return cards.stream()
                    .filter(c -> c.getRoomnumber() == roomNumber && c.getOwner().getName().equalsIgnoreCase(guestName))
                    .findFirst();
        }

        private Optional<room> loadRoom(int roomNumber) {
            File source = resolveDataFile(Main.HOTEL_PATH +"/"+"Room.txt");

            if (!source.exists()) return Optional.empty();

            try (Scanner scanner = new Scanner(source)) {
                if (scanner.hasNextLine()) scanner.nextLine();

                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");

                    if (parts.length >= 2) {
                        int recordedRoom = parseNumber(parts[0].trim());

                        if (recordedRoom == roomNumber) {
                            String type = parts[1].trim();
                            room entry = new room(Main.HOTEL_PATH, recordedRoom, type, "Reserved", null, null);
                            entry.lock();
                            return Optional.of(entry);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                return Optional.empty();
            }

            return Optional.empty();
        }

        private File resolveDataFile(String fileName) {
            String[] prefixes = {"", DATA_ROOT, FALLBACK_ROOT, "Backend/Hotel/untitled/", "Backend/Hotel/"};

            for (String prefix : prefixes) {
                File candidate = prefix.isEmpty() ? new File(fileName) : new File(prefix + fileName);
                if (candidate.exists()) return candidate;
            }

            return new File(DATA_ROOT + fileName);
        }

        private int parseNumber(String value) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                return -1;
            }
        }

        private String rebuildName(String[] tokens) {
            return String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length));
        }
    }
}