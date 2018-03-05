package eu.openeo.backend.wcps;

import java.util.Vector;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import eu.openeo.backend.wcps.domain.Aggregate;
import eu.openeo.backend.wcps.domain.Collection;
import eu.openeo.backend.wcps.domain.Filter;

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
		//this.build(openEOGraph);
	}

	public WCPSQueryFactory(JSONObject openEOGraph, String outputFormat) {
		this(openEOGraph);
		this.outputFormat = outputFormat;
		this.build(openEOGraph);
	}

	private void build(JSONObject openEOGraph) {
		log.debug(openEOGraph.toJSONString());
		parseOpenEOProcessGraph( openEOGraph);
//		if (openEOGraph.containsKey(new String("process_graph"))) {
//			parseOpenEOProcessGraph((JSONObject) openEOGraph.get(new String("process_graph")));
//
//		}
//		if (openEOGraph.containsKey(new String("output"))) {
//			this.outputFormat = (String) ((JSONObject) openEOGraph.get(new String("output"))).get(new String("format"));
//			log.debug("the following output format was found: " + this.outputFormat);
//		}
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
		for (int a = aggregates.size() - 1; a >= 0; a--) {
			if (aggregates.get(a).getAxis().equals("DATE")) {
				wcpsStringBuilder.append(createTempAggWCPSString("$c1", aggregates.get(a)));
			}
			if (aggregates.get(a).getOperator().equals("NDVI")) {
				wcpsStringBuilder.append(createNDVIWCPSString("$c1", aggregates.get(a)));
			}
		}
		if (filters.size() > 0) {
			wcpsStringBuilder.append(createFilteredCollectionString("$c1"));
		}
		// TODO define return type from process tree
		wcpsStringBuilder.append(", \"" + outputFormat + "\" )");
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
		StringBuilder stringBuilder = new StringBuilder("condense ");
		stringBuilder.append(operator + " over $pm t (imageCrsDomain(");
		stringBuilder.append(createFilteredCollectionString(collectionName, tempFilter) + ",");
		stringBuilder.append(axis + ")) using ");
		this.filters.remove(tempFilter);
		this.filters.add(new Filter(axis, "$pm"));
		return stringBuilder.toString();
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
				} else {
					createAggregateFromProcess(processParent);
				}
			} else if (keyStr.equals("args")) {
				JSONObject argsObject = (JSONObject) processParent.get(keyStr);
				for (Object argsKey : argsObject.keySet()) {
					String argsKeyStr = (String) argsKey;
					if (argsKeyStr.equals("collections") || argsKeyStr.equals("imagery")) {
						try {
							JSONArray collections = (JSONArray) argsObject.get(argsKey);
							result = parseOpenEOProcessGraph((JSONObject) collections.get(0));
							if (result == null) {
								result = (JSONObject) collections.get(0);
								break;
							}
						}catch(java.lang.ClassCastException e) {
							JSONObject collections = (JSONObject) argsObject.get(argsKey);
							result = parseOpenEOProcessGraph(collections);
							if (result == null) {
								result = collections;
								break;
							}
						}
						
					}
				}
			} else if (keyStr.equals("collection_id") || keyStr.equals("product_id")) {
				String name = (String) processParent.get(keyStr);
				collectionIDs.add(new Collection(name));
				log.debug("found actual dataset: " + name);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param process
	 */
	private void createFilterFromProcess(JSONObject process) {
		boolean isTemporalFilter = false;
		boolean isBoundBoxFilter = false;
		for (Object key : process.keySet()) {
			String keyStr = (String) key;
			if (keyStr.equals("process_id")) {
				String name = (String) process.get(keyStr);
				log.debug("currently working on: " + name);
				if (name.contains("date")) {
					isTemporalFilter = true;
				} else if (name.contains("bbox")) {
					isBoundBoxFilter = true;
				}
			}
		}
		for (Object key : process.keySet()) {
			String keyStr = (String) key;
			if (keyStr.equals("args")) {
				JSONObject argsObject = (JSONObject) process.get(keyStr);
				if (isTemporalFilter) {
					createDateRangeFilterFromArgs(argsObject);
				}
				if (isBoundBoxFilter) {
					createBoundingBoxFilterFromArgs(argsObject);
				}

			}
		}
	}

	private void createDateRangeFilterFromArgs(JSONObject argsObject) {
		String fromDate = null;
		String toDate = null;
		for (Object argsKey : argsObject.keySet()) {
			String argsKeyStr = (String) argsKey;
			if (argsKeyStr.equals("from")) {
				fromDate = (String) argsObject.get(argsKey);
			} else if (argsKeyStr.equals("to")) {
				toDate = (String) argsObject.get(argsKey);
			}
		}
		if (fromDate != null && toDate != null)
			this.filters.add(new Filter("DATE", fromDate, toDate));
	}

	private void createBoundingBoxFilterFromArgs(JSONObject argsObject) {
		String left = null;
		String right = null;
		String top = null;
		String bottom = null;
		for (Object argsKey : argsObject.keySet()) {
			String argsKeyStr = (String) argsKey;
			if (argsKeyStr.equals("left")) {
				left = "" + argsObject.get(argsKey).toString();
			} else if (argsKeyStr.equals("right")) {
				right = "" + argsObject.get(argsKey).toString();
			}
			if (argsKeyStr.equals("top")) {
				top = "" + argsObject.get(argsKey).toString();
			} else if (argsKeyStr.equals("bottom")) {
				bottom = "" + argsObject.get(argsKey).toString();
			}
		}
		this.filters.add(new Filter("E", left, right));
		this.filters.add(new Filter("N", top, bottom));
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
				} else if (processName.contains("NDVI")) {
					isNDVIAggregate = true;
				}
				break;
			}
		}
		for (Object key : process.keySet()) {
			String keyStr = (String) key;
			if (keyStr.equals("args")) {
				JSONObject argsObject = (JSONObject) process.get(keyStr);
				if (isTemporalAggregate) {
					createTemporalAggregate(processName);
				}
				if (isNDVIAggregate) {
					createNDVIAggregateFromProcess(argsObject);
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
				red = "" + argsObject.get(argsKey).toString();
			} else if (argsKeyStr.equals("nir")) {
				nir = "" + argsObject.get(argsKey).toString();
			}
		}
		Vector<String> params = new Vector<String>();
		params.add(red);
		params.add(nir);
		if (red != null && nir != null)
			aggregates.add(new Aggregate(new String("feature"), new String("NDVI"), params));
	}

}
