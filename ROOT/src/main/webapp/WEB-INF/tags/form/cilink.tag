<%-- 
   -      Renders a link used to view a CI
   -       
   --%><%@ 
attribute name="ci" required="true" type="org.onecmdb.core.IType" %><%@ 
attribute name="history" required="false" %><%@ 
attribute name="action" required="false" %><%@
include file="/WEB-INF/tags/include.tagf" %><%@
taglib prefix="form" tagdir="/WEB-INF/tags/form" %><%@
taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %><c:set
var="icon"><onecmdb:ciicon ci="${ci}" /></c:set><c:choose><c:when
test="${empty ci.id}">${icon}&nbsp;${empty ci ? "<em>empty</em>" : ci.displayName}
</c:when><c:otherwise>${icon}&nbsp;<c:set target="${scratch}" property="ci" 
value="${ci.id}"/><form:actionurl
cssclass="linka${(site.action.predicates.hasCi && site.action.ci == ci) ? 'selected' : ''}" 
navigate="${(empty history) ? ( empty action ? 'viewci' : action ) : history}" 
action="${empty action ? site.actionMap['viewci'] : site.actionMap[action]}" 
params="${scratch}"><onecmdb:ciname ci="${ci}"/></form:actionurl><c:if 
test="${site.debugEnabled}"><sub>(<c:out value="${ci.alias}" />)</sub></c:if
></c:otherwise></c:choose>&nbsp;<onecmdb:mem ci="${ci}" />