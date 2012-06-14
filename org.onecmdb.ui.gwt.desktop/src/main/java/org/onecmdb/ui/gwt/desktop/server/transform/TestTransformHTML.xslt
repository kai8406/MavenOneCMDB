<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<body>
				<h2>AutoNet3 Anslutningarlista</h2>
				<table border="1">
					<tr bgcolor="#9acd32">
						<th align="left">Brukare</th>
						<th align="left">AnslTyp</th>
						<th align="left">LanIP</th>
						<th align="left">LinkIP</th>
					</tr>
					
					<xsl:for-each select="Graph/brukare">
						<tr>
							<td>
								<xsl:value-of select="name" />
							</td>
							<td>
								<xsl:value-of select="a2b/access[a2c/connection/connectionType='TEW']/a2c/connection/connectionType"/>
							</td>
							
							<td>
								<xsl:value-of select="a2b/access[a2c/connection/connectionType='TEW']/a2c/connection/lanIP"/>
							</td>
							<td>
								
							</td>
							
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>