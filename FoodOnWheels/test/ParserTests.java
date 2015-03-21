import static org.junit.Assert.*;

import javax.jdo.PersistenceManager;

import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import com.google.gwt.foodonwheels.server.YelpAPI;
import com.google.gwt.foodonwheels.server.FoodTruckServiceImpl;

public class ParserTests {

	@Test
	public void QueryforYelp(){
		YelpAPI yelp = new YelpAPI();
		String request = yelp.searchForFoodTrucksVancouver();
		Assert.assertNotNull("request is not null", request);
	}
	
	@Test
	public void parsingYelp() throws ParseException{
		FoodTruckServiceImpl service = new FoodTruckServiceImpl();
	}
}
