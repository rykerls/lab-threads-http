import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";
      // Attempts to URL encode user input into OMDB api, catches and returns if an exception is thrown by URLEncoder.encode.
		try {
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie";
		}catch(UnsupportedEncodingException uee){
			return null;
		}

		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		String[] movies = null;

		try {
         
         // Make the URL string an Object, because Java
			URL url = new URL(urlString);
         
         // Opens a TCP connection to URL and connects in "GET" mode
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
         
         // Get reference to incoming data stream from active URLConnection
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			
         // Connection may have been closed.
         // Either way, we aren't getting data
         // So we return nothing
         if (inputStream == null) {
				return null;
			}
         // Pipe the incoming byte stream to BufferedReader, which allows 
         // us to break the data up in a variety of ways.
         // (we're reading it line by line)
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = reader.readLine();
			while (line != null) {
				buffer.append(line + "\n");
				line = reader.readLine();
			}
         // The InputStream remained open, but didn't return any data
			if (buffer.length() == 0) {
				return null;
			}
         
         // Format query results
			String results = buffer.toString();
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");
         
			movies = results.split("\n");
		} 
		catch (IOException e) {
			return null;
		} 
		finally {
         // Close connection to URL
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
         // Close character stream after InputStream has been closed 
			if (reader != null) {
				try {
					reader.close();
				} 
				catch (IOException e) {
				}
			}
		}
      // Return array of movies from OMDB response
		return movies;
	}


	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);

		boolean searching = true;

		while(searching) {					
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().equals("q")){
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm);
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
