<%@include file="common/header.jspf"%>
<style>
    h3, h5, table, a {
        display: flex;
        justify-content: center;
    }
    td {
        border: 1px solid black;
        font-size: 20px;
        padding: 10px;
    }
    a {
        margin-top: 15px;
        text-decoration: none;
    }
    .simulationForm {
        width: 15%;
        margin: 2% auto auto;
    }
    .text-margin {
        margin-left: 8%;
    }
    .button-margin {
        margin-left: 28%;
    }
    .mostSales {
        background-color: #50C878;
    }
</style>
<div class="container">
    <h3>Electronic shop search engine simulation</h3>
    <h5>Days passed: ${numberOfDays}, total number of visitors: ${numberOfCustomers}</h5>

    <table>
        <tr>
            <td>Eshop</td>
            <td>Sales made</td>
            <td>Revenue generated</td>
            <td>Bankrupt sellers</td>
            <td>Bankrupt</td>
            <td>Owner satisfied</td>
        </tr>

        <c:forEach begin="0" end="4" items="${shops}" var="shop">
            <tr class="${shop.mostSales ? "mostSales" : ""}">
                <td>${shop.name}</td>
                <td>${shop.totalSales}</td>
                <td>${shop.generatedRevenue}</td>
                <td>${shop.bankruptSellers}%</td>
                <td>${shop.bankrupt ? "Yes" : "No"}</td>
                <td>${shop.bankruptSellers > 50 ? "No" : "Yes"}</td>
            </tr>
        </c:forEach>
    </table>

    <form:form method="get" cssClass="simulationForm" action="/simulation" modelAttribute="simulationRequest">
        <div class="col-md" style="margin-top: 1%">
            <div class="form-outline mb-4">
                <form:label cssClass="form-label text-margin" path="simulateDays">Enter days to simulate:</form:label>
                <form:input cssClass="form-control" type="text" path="simulateDays" required="required"/>
            </div>
            <button type="submit" class="btn btn-primary btn-block mb-2 button-margin">Submit</button>
        </div>
    </form:form>

    <a href="/simulation/30">
        <input type="submit" class="btn btn-primary btn-block mb-2" value="Simulate one month">
    </a>

    <a href="/simulation/365">
        <input type="submit" class="btn btn-primary btn-block mb-2" value="Simulate one year">
    </a>
</div>