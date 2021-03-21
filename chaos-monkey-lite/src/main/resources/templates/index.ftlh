<#include "common/header.ftl">

<#if containers?size == 0>
No containers to display. Is docker demon running?

<#else>
<h3>List of all containers</h3>
<table class="table">
    <tr>
        <#--<th>Id</th>-->
        <th>Name</th>
        <th>Status</th>
        <th>Available actions</th>
        <th></th>
    </tr>
    <#list containers as container>
        <tr>
            <#--<td>${container.id()}</td>-->
            <td align="left"><#list container.names() as name>${name}</#list></td>
            <td align="left">
                <#if container.status()?contains("Paused")>
                    <i class="fa fa-pause-circle fa-lg" style="color: gold;"></i>
                <#elseif container.status()?contains("Up")>
                    <i class="fa fa-check-circle fa-lg" style="color: green;"></i>
                <#else>
                    <i class="fa fa-power-off fa-lg" style="color: red;"></i>
                </#if>
                ${container.status()}
            </td>
            <td>
                <a href="/remove/${container.id()}"><i class="fa fa-trash fa-2x" style="color: red;"></i></a>

                <#if container.status()?contains("Paused")>
                    <a href="/unpause/${container.id()}"><i class="fa fa-play-circle fa-2x" style="color: green;"></i></a>
                <#elseif container.status()?contains("Up")>
                    <a href="/pause/${container.id()}"><i class="fa fa-pause-circle fa-2x" style="color: gold;"></i></a>
                    <a href="/stop/${container.id()}"><i class="fa fa-stop-circle fa-2x" style="color: red;"></i></a>
                <#else>
                    <a href="/start/${container.id()}"><i class="fa fa-play-circle fa-2x" style="color: green;"></i></a>
                </#if>


            </td>
            <td>
                <!-- https://getbootstrap.com/docs/4.6/components/dropdowns/ -->
                <div class="dropdown">
                    <a class="btn btn-secondary dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Network operations
                    </a>

                    <div class="dropdown-menu" aria-labelledby="dropdownMenuLink">
                        <a class="dropdown-item" href="/delay/${container.id()}/100">Delay packets</a>
                        <a class="dropdown-item" href="/delay/${container.id()}/100">Drop packets</a>
                        <a class="dropdown-item" href="/delay/${container.id()}/100">Corrupt packets</a>
                    </div>
                </div>
            </td>
        </tr>
    </#list>
</table>
</#if>

<#if operations?size == 0>
<#else>
<div class="card">
    <div class="card-header">
        Operations
    </div>
    <div class="card-body">
        <pre>
<#list operations?reverse as operation>
${operation}
</#list>
        </pre>
    </div>
</div>
</#if>


<#macro executeOnLoad>
</#macro>

<#include "common/footer.ftl">