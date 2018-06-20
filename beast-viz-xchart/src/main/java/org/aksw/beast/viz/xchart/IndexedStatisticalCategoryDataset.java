package org.aksw.beast.viz.xchart;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.opencsv.CSVWriter;

public class IndexedStatisticalCategoryDataset<S, C> {
	/**
	 * The available series. (pseudo plural to distinguish from individual items...)
	 * 
	 * Use linked-based collection (e.g. LinkedHashSet) to enforce specific orders
	 */
	protected Set<S> serieses = new LinkedHashSet<>();
    
	
	/**
	 * Mapping from series to label
	 */
	protected Map<S, String> seriesLabelMap = new HashMap<>();
	
	
	/**
	 * The available categories.
	 * 
	 * Use linked-based collection (e.g. LinkedHashSet) to enforce specific orders
	 */
	protected Set<C> categories = new LinkedHashSet<>(); // x axis

	/**
	 * Mapping from series to label
	 */
	protected Map<C, String> categoryLabelMap = new HashMap<>();

	
	/**
	 * Values
	 * 
	 */
	protected Table<S, C, Number> seriesToCategoryToValue = HashBasedTable.create();

	/**
	 * Error Values
	 * 
	 */
	protected Table<S, C, Number> seriesToCategoryToError;


	public IndexedStatisticalCategoryDataset(boolean hasErrorValues) {
		this.seriesToCategoryToError = hasErrorValues ? HashBasedTable.create() : null;
	}
	

	public Set<S> getSerieses() {
		return serieses;
	}

	public IndexedStatisticalCategoryDataset<S, C> setSerieses(Set<S> series) {
		this.serieses = series;
		return this;
	}


	public Set<C> getCategories() {
		return categories;
	}

	public IndexedStatisticalCategoryDataset<S, C> setCategories(Set<C> categories) {
		this.categories = categories;
		return this;
	}

	
	public Table<S, C, Number> getValueTable() {
		return seriesToCategoryToValue;
	}
	
	public IndexedStatisticalCategoryDataset<S, C> setValueTable(Table<S, C, Number> values) {
		this.seriesToCategoryToValue = values;
		return this;
	}
	

	public Table<S, C, Number> getErrorTable() {
		return seriesToCategoryToError;
	}

	public IndexedStatisticalCategoryDataset<S, C> setErrorTable(Table<S, C, Number> errors) {
		this.seriesToCategoryToError = errors;
		return this;
	}
	
	
	public Map<S, String> getSeriesLabelMap() {
		return seriesLabelMap;
	}


	public IndexedStatisticalCategoryDataset<S, C> setSeriesLabelMap(Map<S, String> seriesLabelMap) {
		this.seriesLabelMap = seriesLabelMap;
		return this;
	}


	public Map<C, String> getCategoryLabelMap() {
		return categoryLabelMap;
	}


	public IndexedStatisticalCategoryDataset<S, C> setCategoryLabelMap(Map<C, String> categoryLabelMap) {
		this.categoryLabelMap = categoryLabelMap;
		return this;
	}
	
	
	
	/*
	 * Interface methods
	 */
	


	public String getSeriesLabel(Object series) {
		return seriesLabelMap.get(series);
	}
	
	
	
	public String getCategoryLabel(Object category) {
		return categoryLabelMap.get(category);
	}
	

	
//	public void addSeries(S series) {
//		seriesExt.add(series);
//	}
//
//	public void addCategory(C category) {
//		categoryExt.add(category);
//	}

	
	public void setCategoryLabel(C category, String label) {
		categoryLabelMap.put(category, label);
	}

	public void setSeriesLabel(S series, String label) {
		seriesLabelMap.put(series, label);
	}
	
	public void putValue(S series, C category, Number value) {
		serieses.add(series);
		categories.add(category);
		seriesToCategoryToValue.put(series, category, value);
	}

	public void putError(S series, C category, Number value) {
		serieses.add(series);
		categories.add(category);
		seriesToCategoryToError.put(series, category, value);
	}

	public Number getValue(Object series, Object category) {
		Number result = seriesToCategoryToValue.get(series, category);
		return result;
	}
	public Number getError(Object series, Object category) {
		Number result = seriesToCategoryToError != null ? seriesToCategoryToError.get(series, category) : null;
		return result;
	}
	
	public boolean hasErrors() {
		return this.seriesToCategoryToError != null;
	}
	
	// Combined = error + value
	public static Optional<Number> minCombinedValue(IndexedStatisticalCategoryDataset<?, ?> dataset) {

		Double r = null;
		for(Object series : dataset.getSerieses()) {
			for(Object category : dataset.getCategories()) {
				Number value = dataset.getValue(series, category);
				Number error = dataset.getError(series, category);
				
				Double tmpMin = XChartStatBarChartProcessor.combinedMinValue(value, error);
				if(tmpMin != null) {
					r = r != null
						? Math.min(r.doubleValue(), tmpMin.doubleValue())
						: tmpMin.doubleValue();
				}					
			}
		}
		
		return Optional.ofNullable(r);
	}
	
	public static Optional<Number> maxCombinedValue(IndexedStatisticalCategoryDataset<?, ?> dataset) {

		Double r = null;
		for(Object series : dataset.getSerieses()) {
			for(Object category : dataset.getCategories()) {
				Number value = dataset.getValue(series, category);
				Number error = dataset.getError(series, category);
				
				Double tmpMax = XChartStatBarChartProcessor.combinedMaxValue(value, error);
				if(tmpMax != null) {
					r = r != null
						? Math.max(r.doubleValue(), tmpMax.doubleValue())
						: tmpMax.doubleValue();
				}					
			}
		}
		
		return Optional.ofNullable(r);
	}


	
	public static <S, C> IndexedStatisticalCategoryDataset<S, C> copy(IndexedStatisticalCategoryDataset<S, C> src) {
		IndexedStatisticalCategoryDataset<S, C> result = new IndexedStatisticalCategoryDataset<>(src.hasErrors());
		
		
		result
			.setSerieses(new LinkedHashSet<>(src.getSerieses()))
			.setSeriesLabelMap(new LinkedHashMap<>(src.getSeriesLabelMap()))
			
			.setCategories(new LinkedHashSet<>(src.getCategories()))
			.setCategoryLabelMap(new LinkedHashMap<>(src.getCategoryLabelMap()))
		
			.setValueTable(HashBasedTable.create(src.getValueTable()))
			.setErrorTable(Optional.ofNullable(src.getErrorTable()).map(HashBasedTable::create).orElse(null))
			;
		
		return result;
	}
	
	
	public static <S, C> void toCsv(IndexedStatisticalCategoryDataset<S, C> dataset) {
		Writer writer = new OutputStreamWriter(System.out);
		try(CSVWriter csvWriter = new CSVWriter(writer)) {

			int n = dataset.getSerieses().size() * (dataset.hasErrors() ? 2 : 1) + 1;
	
			List<String> headings = new ArrayList<>(n);
			headings.add("category");
	
			for(S series : dataset.getSerieses()) {
				String label = dataset.getSeriesLabel(series);
				headings.add(label + "_val");
				if(dataset.hasErrors()) {
					headings.add(label + "_err");
				}
			}
			
			csvWriter.writeNext(headings.toArray(new String[0]));

			
			for(C category : dataset.getCategories()) {
				List<String> row = new ArrayList<>(n);
				String categoryLabel = dataset.getCategoryLabel(category);
				
				row.add(categoryLabel);
				for(S series : dataset.getSerieses()) {
					Number value = dataset.getValue(series, category);
					Number error = dataset.getError(series, category);
					
					//BigDecimal v = value == null ? null : new BigDecimal(value.doubleValue());
					Object v = value == null ? null : value.doubleValue();
					row.add(v == null ? "" : v.toString());
					
					if(dataset.hasErrors()) {
						//BigDecimal e = error == null ? null : new BigDecimal(error.doubleValue());
						Object e = error == null ? null : error.doubleValue();
						row.add(e == null ? "" : v.toString());
					}
				}
				csvWriter.writeNext(row.toArray(new String[0]));
			}
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
	}
}
