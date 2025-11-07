<%@ tag description="User Header Component" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="username" required="true" type="java.lang.String" %>
<%@ attribute name="backUrl" required="false" type="java.lang.String" %>
<%@ attribute name="backText" required="false" type="java.lang.String" %>

<div class="user-header">
    <div class="user-info">
        <div class="user-avatar">
            <i class="fas fa-user"></i>
        </div>
        <div class="user-name">
            ${username}
        </div>
    </div>
    <c:choose>
        <c:when test="${not empty backUrl}">
            <a href="${backUrl}" class="back-btn">
                <i class="fas fa-arrow-left"></i> ${not empty backText ? backText : 'Назад'}
            </a>
        </c:when>
        <c:otherwise>
            <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Выйти</a>
        </c:otherwise>
    </c:choose>
</div>