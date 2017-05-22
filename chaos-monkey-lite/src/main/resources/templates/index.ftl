<html>
<body>

<#if containers?size == 0>
No containers to display. Is docker demon running?

<#else>
<h3>List of running containers</h3>
<table>
    <tr>
        <th>Id</th>
        <th>Name</th>
        <th>Status</th>
        <th>Operation</th>
    </tr>
    <#list containers as container>
        <tr>
            <td>${container.id()}</td>
            <td align="left"><#list container.names() as name>${name}</#list></td>
            <td align="left">${container.status()}</td>
            <td>
                <a href="/remove/${container.id()}">Delete</a>
                <a href="#start">Start</a>
                <a href="#stop">Stop</a>
                <a href="#network-failure">Network failure</a>
            </td>
        </tr>
    </#list>
</table>
</#if>
</body>
</html>