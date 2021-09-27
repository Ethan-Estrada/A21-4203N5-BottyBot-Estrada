package dupont.bot;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.CORBA.WStringSeqHelper;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Hello world!
 *
 */
public class App 
{
    public static List<String> ListeSitesVisites =  new ArrayList<String>();
    public static List<String> UrlsNonVisiter = new ArrayList<String>();
    public  static String Site="";
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
                Elements elements = doc.select("a[href]");
                for (Element e : elements) {

                    UrlsNonVisiter.add(e.attr("href"));
                }
                System.out.println("Non visiter"+UrlsNonVisiter);
                System.out.println(ListeSitesVisites);


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
            ArrayList<String> ListEmail = new ArrayList(hashSet);
            Collections.sort(ListEmail);
            System.out.println();
            System.out.println("Nombre de courriels extraits (en ordre alphabetique) : " + ListEmail.size());
            for (int i = 0; i < ListEmail.size(); i++) {
                System.out.print("      " + ListEmail.get(i));
                System.out.println();
            }

            /// Condition de recursion
            for (String Urls: UrlsNonVisiter) {
                if(!Urls.contains("/^(?:([A-Za-z]+):)?(\\/{0,3})([0-9.\\-A-Za-z]+)\n" +
                        "(?::(\\d+))?(?:\\/([^?#]*))?(?:\\?([^#]*))?(?:#(.*))?$/"))
                {
                    List<String> st =  new ArrayList<String>();
                    st.add(Site + Urls);

                    Parcourir(st.get(0),Profondeur+1);
                    ListeSitesVisites.add(st.get(0));
                    UrlsNonVisiter.remove(Urls);
                    st.clear();
                }
                else{
                    exists(Urls);
                    Parcourir(Urls,Profondeur+1);
                    ListeSitesVisites.add(Urls);
                    UrlsNonVisiter.remove(Urls);
                }

            }


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
            System.out.println("sa marche po");
            return false;
        }


    }

    public static void DownloadWebPage(String Url) {
        try {

            String [] PartUrl = Url.split("/");
            String [] PartUrl2 = PartUrl[PartUrl.length -1].split("\\.");
            for(int i=0; i < PartUrl.length -1;i++)
            {
                Site += PartUrl[i]+"/";
            }

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
            System.out.println("Successfully Downloaded.");
        }

        // Exceptions
        catch (MalformedURLException mue) {
            System.out.println("Malformed URL Exception raised");
        } catch (IOException ie) {
            System.out.println("IOException raised");
        }
    }
}
