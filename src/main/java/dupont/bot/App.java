package dupont.bot;

import java.io.File;
import java.io.IOException;
import java.net.*;
import org.apache.commons.validator.routines.UrlValidator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ///Verifie s'il a 3 arguments
        if (args.length ==3 ){
            ///Verifie la pronfondeur
            String Prof = args[0];
            Integer Profondeur = Integer.parseInt(Prof);
            if ( Profondeur < 0 ){
                System.out.println("Veuillez fournir un nombre entier positif comme profondeur.");
            }
            else {
                System.out.println("La profondeur de : "+Prof+" est valide !");
            }

            ///Verifie URL, son format et  son existance
            String UrlDepart = args[1];
            UrlValidator urlValidator = new UrlValidator();
            if (urlValidator.isValid(UrlDepart) && exists(UrlDepart)) {
                System.out.println("URL : "+UrlDepart+ " est valide !");
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

        }
        else
        {
            System.out.println("Veuillez fournir 3 arguments dont; 1.PROFONDEUR: Indique le nombre de couches dans les sites que le bot va parcourir ex: 7 , " +
                    "2.URL: Le lien de départ du bot ex: https://www.cegepmontpetit.ca/  et 3.REPERTOIRE : Indique l'emplacement dans votre système ou les fichiers téléchargés par le bot seront enregistrer " +
                    "ex: C:\\\\Users\\\\ . Un Exemple serait: 6 https://www.cegepmontpetit.ca/ C:\\\\Users\\\\ ");
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
