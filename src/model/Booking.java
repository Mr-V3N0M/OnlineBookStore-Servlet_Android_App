package model;

import java.util.Date;

public class Booking {

    private int bookingId;
    private Book book;
    private String userId;
    private Date startBooking;
    private Date endBooking;

    public Booking(int bookingId, Book book, String userId, Date startBooking, Date endBooking) {
        this.bookingId = bookingId;
        this.book = book;
        this.userId = userId;
        this.startBooking = startBooking;
        this.endBooking = endBooking;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getStartBooking() {
        return startBooking;
    }

    public void setStartBooking(Date startBooking) {
        this.startBooking = startBooking;
    }

    public Date getEndBooking() {
        return endBooking;
    }

    public void setEndBooking(Date endBooking) {
        this.endBooking = endBooking;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", book=" + book +
                ", userId='" + userId + '\'' +
                ", startBooking=" + startBooking +
                ", endBooking=" + endBooking +
                '}';
    }
}