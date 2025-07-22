
import java.util.*;

class Movie {
    String title;
    List<String> showtimes;
    Map<String, Set<String>> availableSeatsPerShowtime;
    double ticketPrice;

    public Movie(String title, List<String> showtimes, int seats, double ticketPrice) {
        this.title = title;
        this.showtimes = showtimes;
        this.ticketPrice = ticketPrice;
        availableSeatsPerShowtime = new HashMap<>();

        for (String time : showtimes) {
            Set<String> seatsSet = new LinkedHashSet<>();
            for (char row = 'A'; row <= 'E'; row++) {
                for (int col = 1; col <= 10; col++) {
                    seatsSet.add(row + String.valueOf(col));
                }
            }
            availableSeatsPerShowtime.put(time, seatsSet);
        }
    }

    public boolean bookTickets(String showtime, List<String> selectedSeats) {
        Set<String> availableSeats = availableSeatsPerShowtime.get(showtime);
        if (availableSeats != null && availableSeats.containsAll(selectedSeats)) {
            availableSeats.removeAll(selectedSeats);
            return true;
        }
        return false;
    }

    public void cancelTickets(String showtime, List<String> seatsToCancel) {
        Set<String> availableSeats = availableSeatsPerShowtime.get(showtime);
        if (availableSeats != null) {
            availableSeats.addAll(seatsToCancel);
        }
    }

    public void displayInfo(int index) {
        System.out.println(index + ". " + title);
        System.out.println("   Showtimes: " + showtimes);
        System.out.println("   Ticket Price: Rs. " + ticketPrice);
        System.out.println();
    }

    public void displayAvailableSeats(String showtime) {
        Set<String> seats = availableSeatsPerShowtime.get(showtime);
        if (seats == null) {
            System.out.println("No such showtime.");
            return;
        }
        System.out.println("Available Seats (" + seats.size() + "): " + seats);
    }
}

class Booking {
    String userName;
    Movie movie;
    String time;
    List<String> selectedSeats;
    int numberOfTickets;
    final double GST_RATE = 0.05;
    final double CST_RATE = 0.05;

    public Booking(String userName, Movie movie, String time, List<String> selectedSeats) {
        this.userName = userName;
        this.movie = movie;
        this.time = time;
        this.selectedSeats = selectedSeats;
        this.numberOfTickets = selectedSeats.size();
    }

    public void displayTicket() {
        double baseAmount = numberOfTickets * movie.ticketPrice;
        double gst = baseAmount * GST_RATE;
        double cst = baseAmount * CST_RATE;
        double totalAmount = baseAmount + gst + cst;

        System.out.println();
        System.out.println("=================================");
        System.out.println("           MOVIE TICKET          ");
        System.out.println("=================================");
        System.out.println("Customer Name : " + userName);
        System.out.println("Movie         : " + movie.title);
        System.out.println("Showtime      : " + time);
        System.out.println("Tickets       : " + numberOfTickets);
        System.out.println("Selected Seats: " + selectedSeats);
        System.out.println("Price per Ticket: Rs. " + movie.ticketPrice);
        System.out.println("---------------------------------");
        System.out.printf("Base Amount   : Rs. %.2f\n", baseAmount);
        System.out.printf("GST (5%%)      : Rs. %.2f\n", gst);
        System.out.printf("CST (5%%)      : Rs. %.2f\n", cst);
        System.out.println("---------------------------------");
        System.out.printf("Total Amount  : Rs. %.2f\n", totalAmount);
        System.out.println("=================================");
        System.out.println("  Enjoy your movie!");
    }

    public void cancelBooking() {
        movie.cancelTickets(time, selectedSeats);
        System.out.println("Booking cancelled successfully for " + userName);
    }
}

public class MovieTicketBooking {
    static Scanner scanner = new Scanner(System.in);
    static List<Movie> movies = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();

    public static void main(String[] args) {
        initMovies();

        System.out.println("Welcome to the Movie Ticket Booking System");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        boolean continueBooking = true;

        while (continueBooking) {
            System.out.println("\nAvailable Movies:");
            for (int i = 0; i < movies.size(); i++) {
                movies.get(i).displayInfo(i + 1);
            }

            int movieChoice = -1;
            while (true) {
                System.out.print("Select a movie (enter number): ");
                if (scanner.hasNextInt()) {
                    movieChoice = scanner.nextInt();
                    if (movieChoice >= 1 && movieChoice <= movies.size()) {
                        break;
                    }
                }
                System.out.println("Invalid input. Please enter a valid movie number.");
                scanner.nextLine();
            }

            Movie selectedMovie = movies.get(movieChoice - 1);

            System.out.println("Available showtimes: " + selectedMovie.showtimes);
            System.out.print("Enter desired showtime (e.g. 1:00 PM): ");
            scanner.nextLine();
            String chosenTime = scanner.nextLine();

            while (!selectedMovie.showtimes.contains(chosenTime)) {
                System.out.print("Invalid showtime. Please enter a valid time from the list: ");
                chosenTime = scanner.nextLine();
            }

            selectedMovie.displayAvailableSeats(chosenTime);

            int numSeats = -1;
            while (true) {
                System.out.print("Enter number of seats to book: ");
                if (scanner.hasNextInt()) {
                    numSeats = scanner.nextInt();
                    if (numSeats > 0 && numSeats <= selectedMovie.availableSeatsPerShowtime.get(chosenTime).size()) {
                        break;
                    }
                }
                System.out.println("Invalid input. Please enter a valid number of seats.");
                scanner.nextLine();
            }
            scanner.nextLine();

            List<String> selectedSeats = new ArrayList<>();
            for (int i = 0; i < numSeats; i++) {
                System.out.print("Enter seat " + (i + 1) + " (e.g., A1): ");
                String seat = scanner.nextLine().toUpperCase();

                while (!selectedMovie.availableSeatsPerShowtime.get(chosenTime).contains(seat) || selectedSeats.contains(seat)) {
                    System.out.print("Seat not available or already selected. Choose another: ");
                    seat = scanner.nextLine().toUpperCase();
                }

                selectedSeats.add(seat);
            }

            if (selectedMovie.bookTickets(chosenTime, selectedSeats)) {
                Booking booking = new Booking(name, selectedMovie, chosenTime, selectedSeats);
                bookings.add(booking);
                booking.displayTicket();
            } else {
                System.out.println("Some seats are already booked. Booking failed.");
            }

            System.out.print("\nDo you want to book another ticket? (yes/no): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            continueBooking = answer.equals("yes");
        }

        System.out.print("\nWould you like to view all your bookings? (yes/no): ");
        String view = scanner.nextLine().trim().toLowerCase();
        if (view.equals("yes")) {
            viewAllBookings();
        }

        System.out.print("Would you like to cancel a booking? (yes/no): ");
        String cancel = scanner.nextLine().trim().toLowerCase();
        if (cancel.equals("yes")) {
            cancelBooking(name);
        }

        System.out.println("\nThank you for using the Movie Ticket Booking System!");
    }

    public static void initMovies() {
        movies.add(new Movie("Inception", Arrays.asList("10:00 AM", "1:00 PM", "5:00 PM"), 50, 200.0));
        movies.add(new Movie("The Dark Knight", Arrays.asList("11:00 AM", "3:00 PM", "7:00 PM"), 40, 180.0));
        movies.add(new Movie("Interstellar", Arrays.asList("9:00 AM", "12:00 PM", "6:00 PM"), 60, 220.0));
    }

    public static void viewAllBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        System.out.println("\n=== All Bookings ===");
        for (Booking b : bookings) {
            b.displayTicket();
        }
    }

    public static void cancelBooking(String userName) {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.userName.equalsIgnoreCase(userName)) {
                userBookings.add(b);
            }
        }

        if (userBookings.isEmpty()) {
            System.out.println("No bookings found under your name.");
            return;
        }

        for (int i = 0; i < userBookings.size(); i++) {
            System.out.println("Booking " + (i + 1) + ":");
            userBookings.get(i).displayTicket();
        }

        int cancelIndex = -1;
        while (true) {
            System.out.print("Enter booking number to cancel: ");
            if (scanner.hasNextInt()) {
                cancelIndex = scanner.nextInt();
                scanner.nextLine();
                if (cancelIndex >= 1 && cancelIndex <= userBookings.size()) {
                    break;
                }
            } else {
                scanner.nextLine();
            }
            System.out.println("Invalid choice. Please enter a valid booking number.");
        }

        Booking toCancel = userBookings.get(cancelIndex - 1);
        toCancel.cancelBooking();
        bookings.remove(toCancel);

        System.out.println("\nRemaining Bookings:");
        boolean found = false;
        for (Booking b : bookings) {
            if (b.userName.equalsIgnoreCase(userName)) {
                b.displayTicket();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No more bookings under your name.");
        }
    }
}
