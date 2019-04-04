package eu.openeo.backend.wcps;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.openeo.backend.wcps.domain.Aggregate;
import eu.openeo.backend.wcps.domain.Collection;
import eu.openeo.backend.wcps.domain.Filter;

import org.gdal.gdal.gdal;
import org.gdal.osr.osrJNI;
import org.gdal.osr.osr;

import java.net.URL;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

public class WCPSQueryFactory {

	private StringBuilder wcpsStringBuilder;
	private Vector<Collection> collectionIDs;
	private Vector<Filter> filters;
	private Vector<Aggregate> aggregates;
	private String outputFormat = "json";

	Logger log = Logger.getLogger(this.getClass());

	/**
	 * Creates WCPS query from openEO process Graph
	 * 
	 * @param openEOGraph
	 */
	public WCPSQueryFactory(JSONObject openEOGraph) {
		collectionIDs = new Vector<Collection>();
		aggregates = new Vector<Aggregate>();
		filters = new Vector<Filter>();
		wcpsStringBuilder = new StringBuilder("for ");
		// this.build(openEOGraph);
	}

	public WCPSQueryFactory(JSONObject openEOGraph, String outputFormat) {
		this(openEOGraph);
		try {
			this.outputFormat = ConvenienceHelper.getRasdamanNameFromOutput(outputFormat);
		} catch (JSONException e) {
			log.error("An error occured while parsing output: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		} catch (IOException e) {
			log.error("An error occured while parsing output: " + e.getMessage());
			StringBuilder builder = new StringBuilder();
			for (StackTraceElement element : e.getStackTrace()) {
				builder.append(element.toString() + "\n");
			}
			log.error(builder.toString());
		}
		this.build(openEOGraph, outputFormat);
	}

	private void build(JSONObject openEOGraph, String outputFormat) {
		log.debug(openEOGraph.toString());
		parseOpenEOProcessGraph(openEOGraph);
		// if (openEOGraph.containsKey(new String("process_graph"))) {
		// parseOpenEOProcessGraph((JSONObject) openEOGraph.get(new
		// String("process_graph")));
		//
		// }
		// if (openEOGraph.containsKey(new String("output"))) {
		// this.outputFormat = (String) ((JSONObject) openEOGraph.get(new
		// String("output"))).get(new String("format"));
		// log.debug("the following output format was found: " + this.outputFormat);
		// }
		for (int c = 1; c <= collectionIDs.size(); c++) {
			wcpsStringBuilder.append("$c" + c);
			if (c > 1) {
				wcpsStringBuilder.append(", ");
			}
		}
		wcpsStringBuilder.append(" in ( ");
		for (int c = 1; c <= collectionIDs.size(); c++) {
			wcpsStringBuilder.append(collectionIDs.get(c - 1).getName() + " ");
		}
		wcpsStringBuilder.append(") return encode ( ");
		// for (int a = aggregates.size() - 1; a >= 0; a--) {

		for (Object key : openEOGraph.keySet()) {
			String keyStr = (String) key;
			if (keyStr.equals("process_id")) {
				String name = (String) openEOGraph.get(keyStr);
				log.debug("currently working on: " + name);
				if (name.contains("stretch_colors")) {

					double min = 0;
					double max = 0;

					for (Object Val : openEOGraph.keySet()) {
						String ValStr = (String) Val;

						if (ValStr.equals("min")) {

							min = (double) openEOGraph.get(ValStr);
						}
						if (ValStr.equals("max")) {

							max = (double) openEOGraph.get(ValStr);

						}
					}

					StringBuilder stretchBuilder = new StringBuilder("(");

					for (int a = 0; a < aggregates.size(); a++) {
						if (aggregates.get(a).getAxis().equals("DATE")) {
							stretchBuilder.append(createTempAggWCPSString("$c1", aggregates.get(a)));
						}
						if (aggregates.get(a).getOperator().equals("NDVI")) {
							stretchBuilder.append(createNDVIWCPSString("$c1", aggregates.get(a)));
						}
					}
					stretchBuilder.append(")");
					String stretchString = stretchBuilder.toString();

					StringBuilder stretchBuilderExtend = new StringBuilder("(unsigned char)(");

					stretchBuilderExtend.append("(" + stretchString + " + " + (-min) + ")");
					stretchBuilderExtend.append("*(255" + "/" + (max - min) + ")");
					stretchBuilderExtend.append(" + 0)");

					String stretchExtendString = stretchBuilderExtend.toString();

					wcpsStringBuilder.append(stretchExtendString);

				} else if (name.contains("linear_stretch")) {

					int min = 0;
					int max = 0;

					for (Object Val : openEOGraph.keySet()) {
						String ValStr = (String) Val;

						if (ValStr.equals("min")) {

							min = (int) openEOGraph.get(ValStr);
						}
						if (ValStr.equals("max")) {

							max = (int) openEOGraph.get(ValStr);

						}
					}

					StringBuilder stretchBuilder = new StringBuilder("(");

					for (int a = 0; a < aggregates.size(); a++) {
						if (aggregates.get(a).getAxis().equals("DATE")) {
							stretchBuilder.append(createTempAggWCPSString("$c1", aggregates.get(a)));
						}
						if (aggregates.get(a).getOperator().equals("NDVI")) {
							stretchBuilder.append(createNDVIWCPSString("$c1", aggregates.get(a)));
						}
					}
					stretchBuilder.append(")");
					String stretchString = stretchBuilder.toString();

					String stretch1 = stretchString.replace("$pm", "$pm1");
					String stretch2 = stretchString.replace("$pm", "$pm2");
					String stretch3 = stretchString.replace("$pm", "$pm3");
					String stretch4 = stretchString.replace("$pm", "$pm4");

					StringBuilder stretchBuilderExtend = new StringBuilder("(");

					stretchBuilderExtend.append(stretch1 + " - " + "min" + stretch2 + ")*((" + max + "-" + min + ")"
							+ "/(max" + stretch3 + "-min" + stretch4 + ")) + 0");

					String stretchExtendString = stretchBuilderExtend.toString();

					wcpsStringBuilder.append(stretchExtendString);

				} else
				{
					for (int a = 0; a < aggregates.size(); a++) {
						if (aggregates.get(a).getAxis().equals("DATE")) {
							wcpsStringBuilder.append(createTempAggWCPSString("$c1", aggregates.get(a)));
						}
						if (aggregates.get(a).getOperator().equals("NDVI")) {
							wcpsStringBuilder.append(createNDVIWCPSString("$c1", aggregates.get(a)));
						}
					}

				}
			}
		}

		if (filters.size() > 0) {
			wcpsStringBuilder.append(createFilteredCollectionString("$c1"));
		}
		// TODO define return type from process tree
		wcpsStringBuilder.append(", \"" + outputFormat + "\" )");
	}

	private String filter_geometry(String collectionName) {

		return collectionName;
	}

	/**
	 * Helper Method to create a string describing an arbitrary filtering as defined
	 * from the process graph
	 * 
	 * @param collectionName
	 * @return
	 */
	private String createFilteredCollectionString(String collectionName) {
		StringBuilder stringBuilder = new StringBuilder(collectionName);
		stringBuilder.append("[");
		for (int f = 0; f < filters.size(); f++) {
			Filter filter = filters.get(f);
			String axis = filter.getAxis();
			String low = filter.getLowerBound();
			String high = filter.getUpperBound();
			stringBuilder.append(axis + "(");
			if (axis.contains("DATE") && !low.contains("$")) {
				stringBuilder.append("\"");
			}
			stringBuilder.append(low);
			if (axis.contains("DATE") && !low.contains("$")) {
				stringBuilder.append("\"");
			}
			if (high != null) {
				stringBuilder.append(":");
				if (axis.contains("DATE")) {
					stringBuilder.append("\"");
				}

				stringBuilder.append(high);
				if (axis.contains("DATE")) {
					stringBuilder.append("\"");
				}
			}
			stringBuilder.append(")");
			if (f < filters.size() - 1) {
				stringBuilder.append(",");
			}
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	/**
	 * Helper Method to create a string describing a single dimension filter as
	 * defined from the process graph
	 * 
	 * @param collectionName
	 * @return
	 */
	private String createFilteredCollectionString(String collectionName, Filter filter) {
		try {
			StringBuilder stringBuilder = new StringBuilder(collectionName);
			stringBuilder.append("[");
			String axis = filter.getAxis();
			String low = filter.getLowerBound();
			String high = filter.getUpperBound();
			stringBuilder.append(axis + "(");
			if (axis.contains("DATE") && !low.contains("$")) {
				stringBuilder.append("\"");
			}
			stringBuilder.append(low);
			if (axis.contains("DATE") && !low.contains("$")) {
				stringBuilder.append("\"");
			}
			if (high != null) {
				stringBuilder.append(":");
				if (axis.contains("DATE")) {
					stringBuilder.append("\"");
				}

				stringBuilder.append(high);
				if (axis.contains("DATE")) {
					stringBuilder.append("\"");
				}
			}
			stringBuilder.append(")");
			stringBuilder.append("]");
			return stringBuilder.toString();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return "";
		}
	}

	private String createNDVIWCPSString(String collectionName, Aggregate ndviAggregate) {
		String redBandName = ndviAggregate.getParams().get(0);
		String nirBandName = ndviAggregate.getParams().get(1);
		String filterString = createFilteredCollectionString(collectionName);
		filterString = filterString.substring(collectionName.length());
		String red = createBandSubsetString(collectionName, redBandName, filterString);
		String nir = createBandSubsetString(collectionName, nirBandName, filterString);
		StringBuilder stringBuilder = new StringBuilder("((double)");
		stringBuilder.append(nir + " - " + red);
		stringBuilder.append(") / ((double)");
		stringBuilder.append(nir + " + " + red);
		stringBuilder.append(")");
		filters.removeAllElements();

		return stringBuilder.toString();
	}

	private String createTempAggWCPSString(String collectionName, Aggregate tempAggregate) {
		String axis = tempAggregate.getAxis();
		String operator = tempAggregate.getOperator();
		Filter tempFilter = null;
		for (Filter filter : this.filters) {
			if (filter.getAxis().equals("DATE")) {
				tempFilter = filter;
			}
		}
		if (tempFilter != null) {
			StringBuilder stringBuilder = new StringBuilder("condense ");
			stringBuilder.append(operator + " over $pm t (imageCrsDomain(");
			stringBuilder.append(createFilteredCollectionString(collectionName, tempFilter) + ",");
			stringBuilder.append(axis + ")) using ");
			this.filters.remove(tempFilter);
			this.filters.add(new Filter(axis, "$pm"));
			return stringBuilder.toString();
		} else {
			for (Filter filter : this.filters) {
				System.err.println(filter.getAxis());
			}
			// TODO this error needs to be communicated to end user
			// meaning no appropriate filter found for running the condense operator in
			// temporal axis.
			return "";
		}
	}

	private String createBandSubsetString(String collectionName, String bandName, String subsetString) {
		StringBuilder stringBuilder = new StringBuilder(collectionName);
		stringBuilder.append(".");
		stringBuilder.append(bandName);
		stringBuilder.append(subsetString);
		return stringBuilder.toString();
	}

	/**
	 * returns constructed query as String object
	 * 
	 * @return String WCPS query
	 */
	public String getWCPSString() {
		return wcpsStringBuilder.toString();
	}

	/**
	 * 
	 * @param processParent
	 * @return
	 */
	private JSONObject parseOpenEOProcessGraph(JSONObject processParent) {
		JSONObject result = null;
		for (Object key : processParent.keySet()) {
			String keyStr = (String) key;
			if (keyStr.equals("process_id")) {
				String name = (String) processParent.get(keyStr);
				log.debug("currently working on: " + name);
				if (name.contains("filter")) {
					createFilterFromProcess(processParent);
				} else if (name.contains("get_collection")) {
					for (Object collName : processParent.keySet()) {
						String collNameStr = (String) collName;
						if (collNameStr.equals("name")) {

							String coll = (String) processParent.get(collNameStr);
							collectionIDs.add(new Collection(coll));
							log.debug("found actual dataset: " + coll);

							JSONObject jsonresp = null;
							try {
								jsonresp = readJsonFromUrl(
										"http://localhost:8080/openEO_0_3_0/openeo/collections/" + coll);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							int srs = 0;
							srs = jsonresp.getInt("eo:epsg");
							log.debug("srs is: " + srs);

							for (Object collFilterName : processParent.keySet()) {
								String collFilterNameStr = (String) collFilterName;
								if (collFilterNameStr.equals("spatial_extent")) {
									createBoundingBoxFilterFromArgs(processParent, srs);
								}
								if (collFilterNameStr.equals("temporal_extent")) {
									JSONArray extentArray = (JSONArray) processParent.get(collFilterNameStr);
									createDateRangeFilterFromArgs(extentArray);
								}
							}

						}
					}
				}
				/*
				 * else if (name.contains("get_collection") && (keyStr.equals("spatial_extent")
				 * || keyStr.equals("temporal_extent"))) {
				 * createFilterFromGetCollection(processParent); }
				 */
				else {
					createAggregateFromProcess(processParent);
				}
			} else if (keyStr.equals("imagery")) {
				JSONObject argsObject = (JSONObject) processParent.get(keyStr);
				result = parseOpenEOProcessGraph(argsObject);
			}
			/*
			 * else if (keyStr.equals("name")) { String name = (String)
			 * processParent.get(keyStr); collectionIDs.add(new Collection(name));
			 * log.debug("found actual dataset: " + name);
			 * 
			 * }
			 */
		}
		return result;
	}

	/**
	 * 
	 * @param process
	 */
	private void createFilterFromProcess(JSONObject process) {
		for (Object key : process.keySet()) {
			String keyStr = (String) key;
			log.debug("" + keyStr);
			if (!keyStr.contains("extent")) {
				log.debug("no spatial or temporal extent defined in filter");
			}
			// check if filter contains spatial information
			else if (process.get("process_id").toString().contains("bbox")) {
				log.debug("spatial extent defined in filter");
				int srs = 0;
				String coll = null;
				try {
					coll = process.getJSONObject("imagery").getString("name").toString();
				} catch (Exception e) {
					log.error("An error occured while searching for collection anem: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
				}
				JSONObject jsonresp = null;
				try {
					jsonresp = readJsonFromUrl("http://localhost:8080/openEO_0_3_0/openeo/collections/" + coll);
				} catch (JSONException e) {
					log.error("An error occured: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
				} catch (IOException e) {
					log.error("An error occured: " + e.getMessage());
					StringBuilder builder = new StringBuilder();
					for (StackTraceElement element : e.getStackTrace()) {
						builder.append(element.toString() + "\n");
					}
					log.error(builder.toString());
				}
				srs = jsonresp.getInt("eo:epsg");
				log.debug("srs is: " + srs);
				if (srs > 0)
					createBoundingBoxFilterFromArgs(process, srs);
			}
			// check if filter contains temporal information
			else if (process.get("process_id").toString().contains("date")) {
				log.debug("temporal extent defined in filter");
				JSONArray extentArray = (JSONArray) process.get(keyStr);
				createDateRangeFilterFromArgs(extentArray);
			}

		}
	}

	private void createDateRangeFilterFromArgs(JSONArray extentArray) {
		String fromDate = null;
		String toDate = null;

		fromDate = extentArray.get(0).toString();
		toDate = extentArray.get(1).toString();

		if (fromDate != null && toDate != null)
			this.filters.add(new Filter("DATE", fromDate, toDate));
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);

			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private void createBoundingBoxFilterFromArgs(JSONObject argsObject, int srs) {
		String left = null;
		String right = null;
		String top = null;
		String bottom = null;
		log.debug("creating spatial extent filter from process");
		for (Object argsKey : argsObject.keySet()) {
			String argsKeyStr = (String) argsKey;
			if (argsKeyStr.equals("extent") || argsKeyStr.equals("spatial_extent")) {
				JSONObject extentObject = (JSONObject) argsObject.get(argsKeyStr);
				for (Object extentKey : extentObject.keySet()) {
					String extentKeyStr = extentKey.toString();

					if (extentKeyStr.equals("west")) {
						left = "" + extentObject.get(extentKeyStr);
					} else if (extentKeyStr.equals("east")) {
						right = "" + extentObject.get(extentKeyStr);
					} else if (extentKeyStr.equals("north")) {
						top = "" + extentObject.get(extentKeyStr);
					} else if (extentKeyStr.equals("south")) {
						bottom = "" + extentObject.get(extentKeyStr);
					}
				}
				SpatialReference src = new SpatialReference();
				src.ImportFromEPSG(4326);
				SpatialReference dst = new SpatialReference();
				dst.ImportFromEPSG(srs);

				log.debug(srs);

				CoordinateTransformation tx = new CoordinateTransformation(src, dst);
				double[] c1 = null;
				double[] c2 = null;
				c1 = tx.TransformPoint(Double.parseDouble(left), Double.parseDouble(bottom));
				c2 = tx.TransformPoint(Double.parseDouble(right), Double.parseDouble(top));
				left = Double.toString(c1[0]);
				top = Double.toString(c2[1]);
				right = Double.toString(c2[0]);
				bottom = Double.toString(c1[1]);
			}
		}
		if (left != null && right != null && top != null && bottom != null) {
			this.filters.add(new Filter("E", left, right));
			this.filters.add(new Filter("N", bottom, top));
		} else {
			log.error("no spatial information could be found in process!");
		}
	}

	/**
	 * 
	 * @param process
	 */
	private void createAggregateFromProcess(JSONObject process) {
		boolean isTemporalAggregate = false;
		boolean isNDVIAggregate = false;
		String processName = null;
		for (Object key : process.keySet()) {
			String keyStr = (String) key;
			if (keyStr.equals("process_id")) {
				processName = (String) process.get(keyStr);
				log.debug("currently working on: " + processName);
				if (processName.contains("date") || processName.contains("time")) {
					isTemporalAggregate = true;
					createTemporalAggregate(processName);
				} else if (processName.contains("NDVI")) {
					isNDVIAggregate = true;
					createNDVIAggregateFromProcess(process);
				}
			}
		}
	}

	private void createTemporalAggregate(String processName) {
		String aggregateType = processName.split("_")[0];
		Vector<String> params = new Vector<String>();
		for (Filter filter : this.filters) {
			if (filter.getAxis().equals("DATE")) {
				params.add(filter.getLowerBound());
				params.add(filter.getUpperBound());
			}
		}
		aggregates.add(new Aggregate(new String("DATE"), aggregateType, params));
	}

	private void createNDVIAggregateFromProcess(JSONObject argsObject) {
		String red = null;
		String nir = null;
		for (Object argsKey : argsObject.keySet()) {
			String argsKeyStr = (String) argsKey;
			if (argsKeyStr.equals("red")) {
				red = "" + argsObject.getString(argsKeyStr);
			} else if (argsKeyStr.equals("nir")) {
				nir = "" + argsObject.getString(argsKeyStr);
			}
		}
		Vector<String> params = new Vector<String>();
		params.add(red);
		params.add(nir);
		if (red != null && nir != null)
			aggregates.add(new Aggregate(new String("feature"), new String("NDVI"), params));
	}

}