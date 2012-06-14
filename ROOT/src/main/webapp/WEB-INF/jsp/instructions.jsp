<%@ include file="include.jsp" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>

<c:if test="${site.debugEnabled}">
<onecmdb:actionheading action="${site.action}" />
</c:if>

<div class="heading">
<table summary="Action Header"><tr>
<td><h4>&nbsp;</h4></td>
</tr></table>
</div>

<div class="instruction">

<p align=left><img src="<c:url
value="/images/instruction-header.jpg" />" alt="OneCMDB Logo"></p>

<table>
<tr>
<td>
<p><b>This is the OneCMDB Admin GUI.</b></p>

<p><em>Note: This page is provided by a web server running locally on your computer. Manual and feedback pages are provided by the onecmdb.org web site.</em></p>

<p><b>If you are using the default Basic Model:</b></p>

<p>Follow the <em>CI</em> link at the left to enter the CMDB. Open the <em>Descendants</em> to traverse the template chain. This database already contains some demonstration CI:s.</p>

<p><b>Tips</b></p>

<p>Run the discover script found on Start menu or in the bin directory to populate OneCMDB with data.</p>

<p>Modify attribute values in CI:s by entering the [Edit] state. Modify the underlying CMDB model by switching to [Designer] mode and then entering the [Edit] state.</p>

<p>Please note that this OneCMDB GUI only exposes a limited set of functions provided by 
the powerful OneCMDB Core.</p>

<p align="center">For complete documentation of OneCMDB, read the Documentation section at onecmdb.org.
<a href="http://www.onecmdb.org/wiki/index.php/Documentation" onClick="window.open('http://www.onecmdb.org/wiki/index.php/Documentation');return false">http://www.onecmdb.org/wiki/index.php/Documentation</a></p>

<p>For more in-depth information about OneCMDB, go to the onecmdb.org site and check
out product descriptions, release plans, roadmaps, demos etc.</p>

</td>
<td align="center">
<p><img src="<c:url value="/images/onecmdb-www.jpg"/>">
<a href="http://www.onecmdb.org/wiki" onClick="window.open('http://www.onecmdb.org/wiki');return false">onecmdb.org</a></p>

<p><b>Feedback on OneCMDB, please!</b><br>
<a href="" onClick="window.open('http://www.onecmdb.org/feedback/good.html','','height=500,width=500');return false">[Good]</a>&nbsp;<a href="" onClick="window.open('http://www.onecmdb.org/feedback/bad.html','','height=500,width=500');return false">[Bad]</a>
<p><b>Report bugs here, please!</b><br>
<a href="http://www.onecmdb.org/wiki/index.php/Bugs" onClick="window.open('http://www.onecmdb.org/wiki/index.php/Bugs');return false">[Submit bug report]</a></p>

</td>
</tr>
</table>
</div>
