/*
 * Lokomo OneCMDB - An Open Source Software for Configuration
 * Management of Datacenter Resources
 *
 * Copyright (C) 2006 Lokomo Systems AB
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * 
 * Lokomo Systems AB can be contacted via e-mail: info@lokomo.com or via
 * paper mail: Lokomo Systems AB, Svärdvägen 27, SE-182 33
 * Danderyd, Sweden.
 *
 */
package org.onecmdb.core.utils.transform.jdbc;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.onecmdb.core.utils.transform.AInstanceSelector;
import org.onecmdb.core.utils.transform.DataSet;
import org.onecmdb.core.utils.transform.IDataSource;
import org.onecmdb.core.utils.transform.IInstance;
import org.onecmdb.core.utils.transform.IInstanceSelector;
import org.onecmdb.core.utils.transform.csv.CSVRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class JDBCInstanceSelector extends AInstanceSelector {

	private JdbcTemplate jdbcTemplate;
	private String sql;
	private String templateColName;
	private int templateCol = -1;
	
	public JDBCInstanceSelector() {
	}
	
	public JDBCInstanceSelector(String name, String template) {
		setName(name);
		setTemplate(template);
	}
	
	public String getTemplateColName() {
		return templateColName;
	}

	public void setTemplateColName(String colName) {
		this.templateColName = colName;
	}

	
	
	public int getTemplateCol() {
		return templateCol;
	}

	public void setTemplateCol(int col) {
		this.templateCol = col;
	}
	
	public void setTemplateColString(String col) {
		this.templateCol  = Integer.parseInt(col);
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<IInstance> getInstances(final DataSet ds) throws IOException {
		IDataSource dataSrc = ds.getDataSource();
		List<IInstance> result = new ArrayList<IInstance>();
		if (dataSrc instanceof JDBCRow) {
			JDBCRow row = (JDBCRow)dataSrc;
			row.setTemplate(findTemplate(row));
			result.add(row);		
		} else if (dataSrc instanceof JDBCDataSourceWrapper) {
			String sqlQuery = getSql();
			if (sqlQuery == null) {
				sqlQuery = ((JDBCDataSourceWrapper)dataSrc).getQuery();
			}
			
			this.jdbcTemplate = ((JDBCDataSourceWrapper)dataSrc).loadJDBCTemplate();
			result = this.jdbcTemplate.query(sqlQuery, new RowMapper() {

				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					ResultSetMetaData meta = rs.getMetaData();
					JDBCRow row = new JDBCRow();
					row.setAutoCreate(isAutoCreate());
					row.setLocalId(rowNum + "");
					row.setDataSet(ds);
					int cols = meta.getColumnCount();
					for (int col = 1; col <= cols; col++) {
						String name = meta.getColumnName(col);
						Object data = rs.getObject(col);
						row.addCol(col, name, data);
					}
					row.setTemplate(findTemplate(row));
					return(row);
				}

			});
		} else {
			throw new IllegalArgumentException("DataSource not supported! Must be instanceof JDBCDataSourceWarpper.");
		}
		return(result);
	}

	protected String findTemplate(JDBCRow row) {
		if (templateColName != null) {
			Object template = row.getCol(templateColName);
			if (template instanceof String) {
				return((String)template);
			}
		}
		if (templateCol >= 0) {
			Object template = row.getCol(templateCol);
			if (template instanceof String) {
				return((String)template);
			}
		}
		return(getTemplate());
	}

}
