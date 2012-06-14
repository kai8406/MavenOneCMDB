<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="iso-8859-1"/>
<xsl:template match="/">
Brukare|AnslType|LanIP|LinkIP
<xsl:for-each select="Graph/brukare">
<xsl:value-of select="name" />
<xsl:text>|</xsl:text>
<xsl:value-of select="a2b/access[a2c/connection/connectionType='TEW']/a2c/connection/connectionType"/>
<xsl:text>|</xsl:text>
<xsl:value-of select="a2b/access[a2c/connection/connectionType='TEW']/a2c/connection/lanIP"/>|
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>