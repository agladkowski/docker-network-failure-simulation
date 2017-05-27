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
                <div class="dropdown">
                    <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                        Network operations
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
                        <li><a href="/start/${container.id()}">Delay</a></li>
                        <li><a href="/start/${container.id()}">Drop packets</a></li>
                        <li><a href="/start/${container.id()}">Corrupt packets</a></li>
                        <#--<li role="separator" class="divider"></li>-->
                        <#--<li><a href="#">Separated link</a></li>-->
                    </ul>
                </div>
            </td>
        </tr>
    </#list>
</table>
</#if>

<#if operations?size == 0>
<#else>
<h3>Operations</h3>
<pre>
<#list operations?reverse as operation>
${operation}
</#list>
</pre>
</#if>


<#macro executeOnLoad>
</#macro>

<#include "common/footer.ftl">