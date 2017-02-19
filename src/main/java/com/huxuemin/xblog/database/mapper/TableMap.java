package com.huxuemin.xblog.database.mapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.huxuemin.xblog.infrastructure.DomainObject;

public class TableMap<T extends DomainObject> {
	private Class<T> domainClass;
	private String tableName;
	private OneToOneColumnMap primaryKeyColumn;  
	private List<OneToOneColumnMap> oneToOneColumnMaps = new ArrayList<OneToOneColumnMap>();
	private List<OneToManyColumnMap> oneToManyColumnMaps = new ArrayList<OneToManyColumnMap>();
	private List<DomainObjectMap> oneToOneDomainObjectMaps = new ArrayList<DomainObjectMap>();
	private List<DomainObjectMap> oneToManyDomainObjectMaps = new ArrayList<DomainObjectMap>();
	
	public TableMap(String tableName,Class<T> domainClass){
		this.domainClass = domainClass;
		this.tableName = tableName;
	}
	
	public void addOneToOneColumn(String columnName,String fieldName){
		OneToOneColumnMap columnMap = new OneToOneColumnMap(columnName,fieldName,this);
		if(!oneToOneColumnMaps.contains(columnMap)){
			if(primaryKeyColumn == null){
				primaryKeyColumn = columnMap;
			}
			oneToOneColumnMaps.add(columnMap);
		}
	}
	
	public void addOneToManyColumn(String columnName,String foreignKeyColumnName,String foreigntableName,String fieldName){
		OneToOneColumnMap columnMap = new OneToOneColumnMap(columnName,fieldName,this);
		OneToManyColumnMap oneToManyColumnMap = new OneToManyColumnMap(columnMap,foreignKeyColumnName,foreigntableName);
		if(!oneToManyColumnMaps.contains(oneToManyColumnMap)){
			oneToManyColumnMaps.add(oneToManyColumnMap);
		}
	}
	
	public void addOneToOneDomainObject(String foreignKeyColumnName,Class<?> foreignKeyDomainClass,String fieldName){
		DomainObjectMap oneToOneDomainObjectMap = new DomainObjectMap(foreignKeyColumnName,foreignKeyDomainClass,this,fieldName);
		if(!oneToOneDomainObjectMaps.contains(oneToOneDomainObjectMap)){
			oneToOneDomainObjectMaps.add(oneToOneDomainObjectMap);
		}
	}
	
	public void addOneToManyDomainObject(String foreignKeyColumnName,Class<?> foreignKeyDomainClass,String fieldName){
		DomainObjectMap oneToManyDomainObjectMap = new DomainObjectMap(foreignKeyColumnName,foreignKeyDomainClass,this,fieldName);
		if(!oneToManyDomainObjectMaps.contains(oneToManyDomainObjectMap)){
			oneToManyDomainObjectMaps.add(oneToManyDomainObjectMap);
		}
	}
	
	public Class<T> getKlass(){
		return this.domainClass;
	}
	
	public void setPrimaryKeyColumn(String columnName, String fieldName){
		this.primaryKeyColumn = new OneToOneColumnMap(columnName,fieldName,this);
		if(!oneToOneColumnMaps.contains(primaryKeyColumn)){
			oneToOneColumnMaps.add(primaryKeyColumn);
		}
	}

	public String primaryKeyWhereClause(){
		return primaryKeyColumn.getColumnName() + " = ? ";
	}
	
	public Object primaryKeyColumnName(){
		return primaryKeyColumn.getColumnName();
	}
	
	public Object primaryKeyValue(Object domainObject){
		return primaryKeyColumn.getValue(domainObject);
	}
	
	public String getTableName(){
		return this.tableName;
	}
	
	public String insertList(){
		StringBuffer result = new StringBuffer("?");
		for(int i = 0;i < oneToOneColumnMaps.size() - 1;i++){
			result.append(",");
			result.append("?");
		}
		return result.toString();
	}
	
	public String columnList(){
		StringBuffer result = new StringBuffer(" ");
		for(Iterator<OneToOneColumnMap> it = getOneToOneColumns();it.hasNext();){
			OneToOneColumnMap columnMap = it.next();
			result.append(columnMap.getColumnName());
			result.append(",");
		}
		result.setLength(result.length() - 1);
		return result.toString();
	}
	
	public String updateList(){
		StringBuffer result = new StringBuffer(" SET ");
		for(Iterator<OneToOneColumnMap> it = getOneToOneColumns();it.hasNext();){
			OneToOneColumnMap column = it.next();
			result.append(column.getColumnName());
			result.append("=?,");
		}
		result.setLength(result.length() - 1);
		return result.toString();
	}
	
	public String getColumnForField(String fieldName){
		for(Iterator<OneToOneColumnMap> it = getOneToOneColumns();it.hasNext();){
			OneToOneColumnMap columnMap = it.next();
			if(columnMap.getFieldName().equals(fieldName)){
				return columnMap.getColumnName();
			}
		}
		return null;
	}
	
	public Iterator<OneToOneColumnMap> getOneToOneColumns(){
		return oneToOneColumnMaps.iterator();
	}
	
	public Iterator<OneToManyColumnMap> getOneToManyColumns(){
		return oneToManyColumnMaps.iterator();
	}
	
	public Iterator<DomainObjectMap> getOnetoOneDomainObjects(){
		return oneToOneDomainObjectMaps.iterator();
	}
	
	public Iterator<DomainObjectMap> getOneToManyDomainObjects(){
		return oneToManyDomainObjectMaps.iterator();
	}
	
	public TableMap<T> buildColumnAnnotation(){
		ColumnAnnotationProcesser.columnToFieldMaps(this);
		return this;
	}
	
}