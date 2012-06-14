<%@ tag 
    display-name="xstime"
    description="Renders a form input field for editing a time attribute" %>
<%@ attribute name="property" required="true" type="java.lang.String" %>
<%@ include file="/WEB-INF/tags/include.tagf" %>
<%@ taglib prefix="form" tagdir="/WEB-INF/tags/form" %>
<%@ taglib prefix="onecmdb" tagdir="/WEB-INF/tags/onecmdb" %>
<spring:bind path="site.action.formParams[${property}]">

<!--  {{{ xs:time -->
<c:set var="isNull" value="${site.action.formParams[property].nullValue}"/>
ISNULL ? ${isNull}



${site.action.formParams[property].hour}

<spring:bind path="site.action.formParams[${property}].hour">
${status.errorCode}
<sup><small>Hour</small></sup><select name="${status.expression}">
<option value=""></option>
<c:forEach var="h" begin="0" end="23">
<option value="${h}"<c:if test="${!status.error && h==status.value}"> selected</c:if> >${h<10 ? '0' :''}${h}</option>
</c:forEach>
</select>
<c:if test="${status.error}">
<span class="error">${status.errorMessage}</span>
</c:if>
</spring:bind>

<spring:bind path="site.action.formParams[${property}].minute">
<sup><small>Minute</small></sup><select name="${status.expression}">
<option value=""></option>
<c:forEach var="m" begin="0" end="59">
<option value="${m}"<c:if test="${!status.error && m==status.value}"> selected</c:if> >${m<10 ? '0' :''}${m}</option>
</c:forEach>
</select>
<c:if test="${status.error}">
<span class="error">${status.errorMessage}</span>
</c:if>
</spring:bind>

<spring:bind path="site.action.formParams[${property}].second">
<sup><small>Second</small></sup><select name="${status.expression}">
<option value=""></option>
<c:forEach var="s" begin="0" end="59">
<option value="${s}"<c:if test="${!status.error && s==status.value}"> selected</c:if> >${s<10 ? '0' :''}${s}</option>
</c:forEach>
</select>
<c:if test="${status.error}">
<span class="error">${status.errorMessage}</span>
</c:if>
</spring:bind>

<spring:bind path="site.action.formParams[${property}].timezone">
<sup><small>Timezone</small></sup><select name="${status.expression}">
<option value=""></option>
<c:forEach var="tz"  begin="0" end="${24 * 60}" step="60">
<option value="${tz-12*60}"<c:if test="${!status.error && tz-12*60==status.value}"> selected</c:if> >
<fmt:formatNumber pattern="+0000;-0000" value="${(tz-12*60) / 60 * 100 + ((tz-12*60) % 60)}" />
</option>
</c:forEach>
</select>
<c:if test="${status.error}">
<span class="error">${status.errorMessage}</span>
</c:if>
</spring:bind>

<spring:bind path="site.action.formParams[${property}].nullValue">
	<small>Clear<input type="checkbox" value="true" name="${status.expression}"
	<c:if test="${status.value}">checked</c:if>></small>
</spring:bind>





<!--  }}} -->
</spring:bind>