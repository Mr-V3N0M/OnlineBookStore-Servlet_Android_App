package model;

import bcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String URL; // url del DB letto da web.xml
    private final String USER; // login utente da usare per connettersi letto da web.xml
    private final String PWD; // password utente letto da web.xml

    public Model(String dbUrl, String user, String pwd) {
        URL = dbUrl;
        USER = user;
        PWD = pwd;
        registerDriver();
    }

    public void registerDriver() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Checks if a user exist or not.
     *
     * @param username username account
     * @param pwdCandidate candidate password account
     * @return -1 if and only if doesn't exist
     *          0 if and only if exist and he is admin
     *          1 if and only of exist and he isn't admin
     */
    public int checkUser(String username, String pwdCandidate) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        int check = -1;
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            rs = st.executeQuery("SELECT * FROM library.\"user\" WHERE username = '" + username + "'");
            if (rs.next() && BCrypt.checkpw(pwdCandidate, rs.getString("password"))) {
                if (rs.getBoolean("admin")) {
                    check = 0;
                } else {
                    check = 1;
                }
            } else
                check = -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return check;
    }

    public User getUser(String username) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        User user = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            rs = st.executeQuery("SELECT * FROM library.user WHERE username like '" + username + "'");
            while (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("email"), rs.getString("name"), rs.getString("surname"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return user;
    }

    public List<Book> searchBook(String title, String author, String publisher) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();
        Book book;
        if (title == null)
            title = "";
        if (author == null)
            author = "";
        if (publisher == null)
            publisher = "";
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            rs = st.executeQuery("SELECT * FROM library.book WHERE lower(title) like lower('%" + title + "%') AND lower(author) like lower('%" + author + "%') AND lower(publisher) like lower('%" + publisher + "%')");
            while (rs.next()) {
                book = new Book(rs.getInt("bookid"), rs.getString("title"), rs.getString("author"), rs.getString("publisher"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return books;
    }


    public Book getBook(String title, String author, String publisher) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        Book book = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            rs = st.executeQuery("SELECT * FROM library.book WHERE title LIKE '" + title + "' AND author LIKE '" + author + "' AND publisher LIKE '" + publisher + "'");
            while (rs.next()) {
                book = new Book(rs.getInt("bookid"), rs.getString("title"), rs.getString("author"), rs.getString("publisher"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return book;
    }


    public Book getBook(int idBook) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        Book book = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            rs = st.executeQuery("SELECT * FROM library.book WHERE bookid = '" + idBook + "'");
            while (rs.next()) {
                book = new Book(rs.getInt("bookid"), rs.getString("title"), rs.getString("author"), rs.getString("publisher"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return book;
    }


    public List<Book> getAllBooks() {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        Book book;
        List<Book> books = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO biblioteca");
            rs = st.executeQuery("SELECT * FROM library.book WHERE title <> 'null'");
            while (rs.next()) {
                book = new Book(rs.getInt("bookid"), rs.getString("title"),
                        rs.getString("author"), rs.getString("publisher"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return books;
    }

    public List<Book> getAllAvailableBooks() {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        Book book;
        List<Book> books = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO biblioteca");
            rs = st.executeQuery("SELECT * FROM library.book WHERE bookid NOT IN" +
                    " (SELECT bookid FROM library.booking WHERE end_booking ISNULL AND booking.bookid NOTNULL)" +
                    " AND title <> 'null'");
            while (rs.next()) {
                book = new Book(rs.getInt("bookid"), rs.getString("title"),
                        rs.getString("author"), rs.getString("publisher"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return books;
    }

    public List<Book> getAllNotAvailableBooks() {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        Book book;
        List<Book> books = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO biblioteca");
            rs = st.executeQuery("SELECT * FROM library.book WHERE bookid IN (SELECT bookid FROM library.booking WHERE end_booking ISNULL)");
            while (rs.next()) {
                book = new Book(rs.getInt("bookid"), rs.getString("title"), rs.getString("author"), rs.getString("publisher"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return books;
    }

    public List<Booking> getActiveBookingUser(String user) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        Booking booking;
        List<Booking> bookings = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            rs = st.executeQuery("SELECT bookingid, userid, book.bookid, title, author, publisher, start_booking, end_booking" +
                    " FROM library.booking INNER JOIN book ON booking.bookid = book.bookid" +
                    " WHERE userid = '" + user + "' AND end_booking ISNULL");
            while (rs.next()) {
                booking = new Booking(rs.getInt("bookingid"), new Book(rs.getInt("bookid"), rs.getString("title"), rs.getString("author"), rs.getString("publisher")), rs.getString("userid"), rs.getDate("start_booking"), rs.getDate("end_booking"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return bookings;
    }

    public Booking getLastActiveBookingUser(String user) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        Booking booking = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            rs = st.executeQuery("SELECT bookingid, userid, book.bookid, title, author, publisher, start_booking, end_booking" +
                    " FROM library.booking INNER JOIN book ON booking.bookid = book.bookid" +
                    " WHERE userid = '" + user + "' AND end_booking ISNULL AND bookingid >= ALL (SELECT bookingid" +
                    " FROM library.booking INNER JOIN book ON booking.bookid = book.bookid" +
                    " WHERE userid = '" + user + "' AND end_booking ISNULL)");
            while (rs.next()) {
                booking = new Booking(rs.getInt("bookingid"), new Book(rs.getInt("bookid"), rs.getString("title"), rs.getString("author"), rs.getString("publisher")), rs.getString("userid"), rs.getDate("start_booking"), rs.getDate("end_booking"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return booking;
    }


    public List<Booking> getEndedBookingUser(String user) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        Booking booking;
        List<Booking> bookings = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            rs = st.executeQuery("SELECT bookingid, userid, book.bookid, COALESCE(title, 'n/d') AS title, COALESCE(author, 'n/d') AS author, COALESCE(publisher, 'n/d') AS publisher, start_booking, end_booking" +
                    " FROM library.booking LEFT JOIN book ON booking.bookid = book.bookid" +
                    " WHERE userid = '" + user + "' AND end_booking NOTNULL OR (userid = '" + user + "' AND booking.bookid ISNULL AND end_booking ISNULL)");
            while (rs.next()) {
                booking = new Booking(rs.getInt("bookingid"), new Book(rs.getInt("bookid"), rs.getString("title"), rs.getString("author"), rs.getString("publisher")), rs.getString("userid"), rs.getDate("start_booking"), rs.getDate("end_booking"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return bookings;
    }


    public Booking getLastEndedBookingUser(int bookingId) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        Booking booking = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            rs = st.executeQuery("SELECT bookingid, userid, book.bookid, title, author, publisher, start_booking, end_booking" +
                    " FROM library.booking INNER JOIN book ON booking.bookid = book.bookid" +
                    " WHERE bookingid ='" + bookingId + "'");
            while (rs.next()) {
                booking = new Booking(rs.getInt("bookingid"), new Book(rs.getInt("bookid"), rs.getString("title"), rs.getString("author"), rs.getString("publisher")), rs.getString("userid"), rs.getDate("start_booking"), rs.getDate("end_booking"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return booking;
    }


    public boolean booking(int idBook, String idUser) {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        boolean booked = false;
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            rs = st.executeQuery("SELECT * FROM library.booking WHERE bookid = '" + idBook + "' AND end_booking ISNULL");
            if (!rs.next()) {
                st.executeUpdate("INSERT INTO library.booking (bookid, userid, start_booking, end_booking) VALUES ('" + idBook + "', '" + idUser + "', now(), null)");
                booked = true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnResultSet(conn, st, rs);
        }
        return booked;
    }

    public boolean endBooking(int bookingId) {
        Connection conn = null;
        Statement st = null;
        boolean endBooking = false;
        try {
            conn = DriverManager.getConnection(URL, USER, PWD);
            st = conn.createStatement();
            st.execute("SET SEARCH_PATH TO library");
            if (st.executeUpdate("UPDATE library.booking SET end_booking = now() WHERE bookingid = '" + bookingId + "'") == 1)
                endBooking = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnStatement(conn, st);
        }
        return endBooking;
    }

    public void closeConnResultSet(Connection conn, Statement st, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (st != null)
                st.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void closeConnStatement(Connection conn, Statement st) {
        try {
            if (st != null)
                st.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}