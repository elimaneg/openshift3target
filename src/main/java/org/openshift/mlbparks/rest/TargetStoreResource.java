package org.openshift.mlbparks.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.openshift.mlbparks.domain.TargetStore;
import org.openshift.mlbparks.mongo.DBConnection;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@RequestScoped
@Path("/parks")
public class TargetStoreResource {

	@Inject
	private DBConnection dbConnection;

	private DBCollection getTargetStoresCollection() {
		DB db = dbConnection.getDB();
		DBCollection storeListCollection = db.getCollection("stores");

		return storeListCollection;
	}

	private TargetStore populateStoreInformation(DBObject dataValue) {
		TargetStore theStore = new TargetStore();
		theStore.setName(dataValue.get("Name"));
		theStore.setPosition(dataValue.get("pos"));
		theStore.setId(dataValue.get("_id").toString());

		return theStore;
	}

	@GET()
	@Produces("application/json")
	public List<TargetStore> getAllStores() {
		ArrayList<TargetStore> allStoresList = new ArrayList<TargetStore>();

		DBCollection targetStores = this.getTargetStoresCollection();
		DBCursor cursor = targetStores.find();
		try {
			while (cursor.hasNext()) {
				allStoresList.add(this.populateStoreInformation(cursor.next()));
			}
		} finally {
			cursor.close();
		}

		return allStoresList;
	}

	@GET
	@Produces("application/json")
	@Path("within")
	public List<TargetStore> findParksWithin(@QueryParam("lat1") float lat1,
			@QueryParam("lon1") float lon1, @QueryParam("lat2") float lat2,
			@QueryParam("lon2") float lon2) {

		ArrayList<TargetStore> allStoresList = new ArrayList<TargetStore>();
		DBCollection targetStores = this.getTargetStoresCollection();

		// make the query object
		BasicDBObject spatialQuery = new BasicDBObject();

		ArrayList<double[]> boxList = new ArrayList<double[]>();
		boxList.add(new double[] { new Float(lon2), new Float(lat2) });
		boxList.add(new double[] { new Float(lon1), new Float(lat1) });

		BasicDBObject boxQuery = new BasicDBObject();
		boxQuery.put("$box", boxList);

		spatialQuery.put("pos", new BasicDBObject("$within", boxQuery));
		System.out.println("Using spatial query: " + spatialQuery.toString());

		DBCursor cursor = targetStores.find(spatialQuery);
		try {
			while (cursor.hasNext()) {
				allStoresList.add(this.populateStoreInformation(cursor.next()));
			}
		} finally {
			cursor.close();
		}

		return allStoresList;
	}
}
