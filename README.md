#KitchenSync Server
Spring Macalester 2014. COMP 225. This repository holds the server-side code for our android mobile application KitchenSync. The app code is located at http://github.com/NaOHman/KitchenSync
##Dependencies
Spark, Maven, Hibernate, JSon, JSoup, Postgress, Heroku
##Architecture
Our server is hosted by heroku. We've scheduled an hourly job that launches the CafeMacParser which connects to cafe mac's database and then creates a week object. The website is parsed using JSoup. The constructor of each of the model classes (Week, Day, Meal, Station, and Food) takes the relevant bit of HTML and then creates an object that corresponds. Then the week is cleaned which makes duplicate references point to the same object. Finally we merge the week with the data already stored in our database and then use Hibernate to save it

The CafeMacServer class provides the interface that our app connects with. It has a get method which returns a json object version of our week's menu. It also has a Post method which allows the app to post reviews to the database. **Do not change the model classes without making corresponding changes to the app**
##Testing
We tested different cases such as a blank url or a completely different url with a different template and so on to test our server. We also did many trial and errors on the localhost trying different queries to ensure that our server would not break. 