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
    public static List<String> ListeSitesVisites =  new ArrayList<>();
    public static List<String> ListeUrlsNonVisiter = new ArrayList<>();
    public static List<String>  ListeEmailFinale = new ArrayList<>();
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

            ///Verifie URL, son format et son existance
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
            PrintEmails();

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

            /// Telechargement du site visiter
            DownloadWebPage(Url);

            /// Recuperer les urls
            Document doc = null;
            try {
                doc = Jsoup.connect(Url).get();
                Elements elements = doc.select("[href]");
                for (Element e : elements) {
                    ListeUrlsNonVisiter.add(e.attr("abs:href"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            ListeSitesVisites.add(Url);

            int compteur=0;
            /// Condition de recursion
                try{
                    // while est pas vide
                    Iterator<String> iter = ListeUrlsNonVisiter.iterator();
                    while (iter.hasNext()) {
                        String str = iter.next();
                        Parcourir(str, Profondeur + 1);
                        iter.remove();
                        compteur++;
                    }
                    System.out.println("Nombre de pages explorees : "+ compteur);
                }
                catch ( ConcurrentModificationException e){
                    e.printStackTrace();
                }

            /// Recuperer les emails
            Pattern p = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
            Matcher matcher = p.matcher(doc.text());
            while (matcher.find()) {
                ListeEmailFinale.add(matcher.group());
            }
            /// Trier en ordre alphabetique la liste et elimine les emails duplices
            Set<String> hashSet = new HashSet<>(ListeEmailFinale);
            ListeEmailFinale.clear();
            ListeEmailFinale.addAll(hashSet);
            Collections.sort(ListeEmailFinale);
        }
    }

    /// Methode qui verifie si un url existe
    public static boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            System.out.println("Page inaccessible << "+URLName);
            return false;
        }

    }
    /// Methode qui telecharge une page web
    public static void DownloadWebPage(String Url) {
        try {
            String [] PartUrl = Url.split("/");
            String [] PartUrl2 = PartUrl[PartUrl.length -1].split("\\.");
            // Create URL object
            URL url = new URL(Url);
            BufferedReader readr = new BufferedReader(new InputStreamReader(url.openStream()));
            // Enter filename in which you want to download
            BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\Test\\"+PartUrl2[0]+".html"));

            // read each line from stream till end
            String line;
            while ((line = readr.readLine()) != null) {
                Pattern p = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
                Matcher matcher = p.matcher(line);

                line = matcher.replaceAll("LOLOLOL@HACKERMAN.EZ");
                writer.write(line);
            }
            readr.close();
            writer.close();
        }

        // Exceptions global du code
        catch (MalformedURLException mue) {
            System.out.println("Url mal formée << " +Url);
        } catch (IOException ie) {
            System.out.println("Page inaccessible << "+Url);
        }
    }
    public static void PrintEmails(){
        /// Imprime les emails recuperer
        System.out.println();
        System.out.println("Nombre de courriels extraits (en ordre alphabetique) : " + ListeEmailFinale.size());
        for (String email: ListeEmailFinale) {
            System.out.print("      " + email);
            System.out.println();
        }
    }
}
