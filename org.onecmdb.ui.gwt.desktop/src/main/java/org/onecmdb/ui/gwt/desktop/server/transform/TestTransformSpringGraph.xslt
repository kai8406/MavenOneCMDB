<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" encoding="iso-8859-1" />

<xsl:template match="/">
<Graph>	
	<xsl:for-each select="Graph/Nodes/Node">
		<node>
			<id>
			<xsl:value-of select="*/@alias" />
			</id>
			<name>
			<xsl:value-of select="*/@displayValue" />
			</name>
		</node>
	</xsl:for-each>
	<xsl:for-each select="Graph/Edges/Edge">
		<node>
			<target>
			<xsl:value-of select="*/target/ref/@alias" />
			</target>
			<source>
			<xsl:value-of select="*/source/ref/@alias" />
			</source>
		</node>
	</xsl:for-each>
	
</Graph>	
</xsl:template>

</xsl:stylesheet>

