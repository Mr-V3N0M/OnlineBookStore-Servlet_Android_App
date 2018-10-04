package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class Servlet extends HttpServlet {

    private Model model;

    public void init(ServletConfig conf) throws ServletException {
        super.init(conf);
        String url = "jdbc:postgresql://localhost:8432/dbnomecognome"; //TODO modificare con il proprio account
        String user = "dbnomecognome"; //TODO modificare con il proprio account
        String pwd = "dbnomecognome"; //TODO modificare con il proprio account
        model = new Model(url, user, pwd);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();

        try {
            StringBuilder sb = new StringBuilder();
            String s;
            // Leggo il json inviato dall'app
            while ((s = request.getReader().readLine()) != null) {
                sb.append(s);
            }
            System.out.println(sb);


            if (sb.toString().contains("login")) {

                Login login = gson.fromJson(sb.toString(), Login.class);
                Status status = new Status();
                System.out.println(login.getUsername() + " " + login.getPassword());
                int isUser = model.checkUser(login.getUsername(), login.getPassword());
                if (isUser != -1) {
                    System.out.println("Credenziali corrette");
                    User user = model.getUser(login.getUsername());

                    // Crea un json con i dati di User
                    String userInfo = gson.toJson(user);
                    System.out.println(userInfo);
                    response.getOutputStream().print(userInfo);
                    response.getOutputStream().flush();
                } else {
                    status.setSuccess(false);
                    status.setDescription("failure");
                    response.getOutputStream().print(gson.toJson(status));
                    response.getOutputStream().flush();
                }

                System.out.println("SONO IN LOGIN");

            } else if (sb.toString().contains("dashboard")) {

                List<Book> allBook = model.getAllBooks();
                response.getOutputStream().print(gson.toJson(allBook));
                response.getOutputStream().flush();

                System.out.println("SONO IN DASHBOARD");

            } else if (sb.toString().contains("prenota")) {

                List<Book> allBook = model.getAllAvailableBooks();
                response.getOutputStream().print(gson.toJson(allBook));
                response.getOutputStream().flush();

                System.out.println("SONO IN PRENOTA");

            } else if (sb.toString().contains("controlloSePrenotabile")) {

                gson.toJson(sb);
                String username = "";
                int idLibro = 0;
                boolean controllo = false;

                // prendo i dati dal json
                JsonObject jobj = new Gson().fromJson(sb.toString(), JsonObject.class);
                idLibro = jobj.get("cspID").getAsInt();
                username = jobj.get("cspUsername").getAsString();

                // controllo se il libro è prenotabie
                controllo = model.booking(idLibro, username);

                // aggiungo i dati aggiornati nel json
                jobj.addProperty("Prenotabile?", controllo);

                // invio la risposta all'applicazione
                response.getOutputStream().print(jobj.toString());
                response.getOutputStream().flush();

                System.out.println("SONO IN CONTROLLOSEPRENOTABILE");

            } else if (sb.toString().contains("restituisci")) {

                gson.toJson(sb);
                String username = "";

                // Prendo i dati dal json
                JsonObject jobj = new Gson().fromJson(sb.toString(), JsonObject.class);
                username = jobj.get("username").getAsString();

                // Aggiungo i nuovi dati nel json
                List<Booking> allBook = model.getActiveBookingUser(username);
                System.out.println(gson.toJson(allBook)); // todo da togliere

                // Invio la risposta indietro all'app
                response.getOutputStream().print(gson.toJson(allBook));
                response.getOutputStream().flush();

                System.out.println("SONO IN RESTITUISCI");

            } else if (sb.toString().contains("riepilogoPrenotazione")) {

                gson.toJson(sb);
                Booking riepilogo = null;
                String username = "";

                // Prendo i dati dal json
                JsonObject jobj = new Gson().fromJson(sb.toString(), JsonObject.class);
                username = jobj.get("Username").getAsString();
                System.out.println(username + " ha prenotato il libro");

                // Prendo i dati del libro prenotato dall'utente
                riepilogo = model.getLastActiveBookingUser(username);

                // Invia la risposta all'app
                response.getOutputStream().print(gson.toJson(riepilogo));
                response.getOutputStream().flush();

                System.out.println("SONO IN RIEPILOGO_PRENOTAZIONE");

            } else if (sb.toString().contains("finePrenotazione")) {

                gson.toJson(sb);
                int idPrenotazione = 0;
                boolean controllo = false;

                // Prendo i dati dal json
                JsonObject jobj = new Gson().fromJson(sb.toString(), JsonObject.class);
                idPrenotazione = jobj.get("id_booking").getAsInt();

                // Controllo che il libro si possa prenotare con successo e aggiungo true o false al json
                controllo = model.endBooking(idPrenotazione);
                jobj.addProperty("success", controllo);
                System.out.println(jobj.toString());

                // Invio il json all'app
                response.getOutputStream().print(jobj.toString());
                response.getOutputStream().flush();

                System.out.println("SONO IN FINE_RESTITUZIONE");

            } else if (sb.toString().contains("prestitiAttivi")) {

                gson.toJson(sb);
                String username = "";

                // Prendo i dati dal json
                JsonObject jobj = new Gson().fromJson(sb.toString(), JsonObject.class);
                username = jobj.get("username").getAsString();

                // Ottengo tutti i prestiti attivi dell'utente
                List<Booking> allBook = model.getActiveBookingUser(username);

                // Invio il json all'app
                response.getOutputStream().print(gson.toJson(allBook));
                response.getOutputStream().flush();

                System.out.println("SONO IN PRESTITI_ATTIVI");

            } else if (sb.toString().contains("prestitiPassati")) {

                gson.toJson(sb);
                String username = "";

                // Prendo i dati dal json
                JsonObject jobj = new Gson().fromJson(sb.toString(), JsonObject.class);
                username = jobj.get("username").getAsString();

                // Ottengo tutti i prestiti passati dell'utente
                List<Booking> allBook = model.getEndedBookingUser(username);

                // Invio la risposta all'app
                response.getOutputStream().print(gson.toJson(allBook));
                response.getOutputStream().flush();

                System.out.println("SONO IN PRESTITI_PASSATI");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}