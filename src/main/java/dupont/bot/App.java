package dupont.bot;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Hello world!
 *
 */
public class App 
{
    public static List<String> ListeSitesVisites =  new ArrayList<String>();

    public static  int ProfondeurMax;


    public static void main( String[] args )
    {
        ///Verifie s'il a 3 arguments
        if (args.length == 3 ){
            ///Verifie la pronfondeur
            String Prof = args[0];
            ProfondeurMax = Integer.parseInt(Prof);
            if ( ProfondeurMax < 0 ){
                System.out.println("Veuillez fournir un nombre entier positif comme profondeur.");
            }
            else {
                System.out.println("La profondeur de : "+ProfondeurMax+" est valide !");
            }

            ///Verifie URL, son format et  son existance
            String UrlDepart = args[1];
            UrlValidator urlValidator = new UrlValidator();
            if (urlValidator.isValid(UrlDepart) && exists(UrlDepart)) {
                System.out.println("URL de départ : "+UrlDepart+ " est valide !");
            } else {
                System.out.println("Veuillez fournir un URL valide");
            }

            ///Verifie si le dossier est accessible et on peux y écrire
            String Repertoire = args[2];
            try {
                File monFichier = new File(Repertoire+"filename.txt");
                monFichier.createNewFile();
                System.out.println("Le repertoire " + Repertoire + " est valide !");
                monFichier.delete();
            } catch (IOException e) {
                System.out.println("Une erreure est survenue avec le repertoire : " + Repertoire);
                e.printStackTrace();
            }
            System.out.println();
            System.out.println("Tout va bien, explorons !");

            Parcourir(UrlDepart,0);


        }
        else
        {
            System.out.println("Veuillez fournir 3 arguments dont; 1.PROFONDEUR: Indique le nombre de couches dans les sites que le bot va parcourir ex: 7 , " +
                    "2.URL: Le lien de départ du bot ex: https://www.cegepmontpetit.ca/  et 3.REPERTOIRE : Indique l'emplacement dans votre système ou les fichiers téléchargés par le bot seront enregistrer " +
                    "ex: C:\\\\Users\\\\ . Un Exemple serait: 6 https://www.cegepmontpetit.ca/ C:\\\\Users\\\\ ");
        }


    }
    public  static  void Parcourir(String Url,int Profondeur)
    {
        if ( Profondeur <= ProfondeurMax && !ListeSitesVisites.contains(Url)) {
            System.out.println("Exploration de >> "+ Url );
            DownloadWebPage(Url);
            /// Recuperer les urls
            Document doc = null;
            try {
                doc = Jsoup.connect(Url).get();
                List<String> UrlsNonVisiter = new ArrayList<String>();
                Elements elements = doc.select("a[href]");
                for (Element e : elements) {

                    UrlsNonVisiter.add(e.attr("href"));
                }
                System.out.println(UrlsNonVisiter);
                ListeSitesVisites.add(Url);
            } catch (IOException e) {
                e.printStackTrace();
            }



            /// Recuperer les emails
            Pattern p = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
            Matcher matcher = p.matcher(doc.text());
            List<String> emails = new ArrayList<String>();
            while (matcher.find()) {
                emails.add(matcher.group());
            }
            /// Trier en ordre alphabetique la liste et elimine les emails duplices
            Set<String> hashSet = new LinkedHashSet<>(emails);
            ArrayList<String> email = new ArrayList(hashSet);
            Collections.sort(email);
            System.out.println();
            System.out.println("Nombre de courriels extraits (en ordre alphabetique) : " + email.size());
            for (int i = 0; i < email.size(); i++) {
                System.out.print("      " + email.get(i));
                System.out.println();
            }


        }


    }


    public static void DownloadWebPage(String webpage) {
        try {

            // Create URL object
            URL url = new URL(webpage);
            BufferedReader readr =
                    new BufferedReader(new InputStreamReader(url.openStream()));

            // Enter filename in which you want to download
            BufferedWriter writer =
                    new BufferedWriter(new FileWriter("D:\\\\Test\\\\Download.html"));

            // read each line from stream till end
            String line;
            while ((line = readr.readLine()) != null) {
                writer.write(line);
            }

            readr.close();
            writer.close();
            System.out.println("Successfully Downloaded.");
        }

        // Exceptions
        catch (MalformedURLException mue) {
            System.out.println("Malformed URL Exception raised");
        } catch (IOException ie) {
            System.out.println("IOException raised");
        }
    }

    public static boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
