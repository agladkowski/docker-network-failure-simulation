<#include "common/header.ftl">

<#if containers?size == 0>
No containers to display. Is docker demon running?

<#else>
<h3>Containers</h3>
<table class="table">
    <tr>
        <th>Status</th>
        <th>Name</th>
        <th>Actions</th>
        <th>Delay Packets</th>
        <th>Drop Packets</th>
        <th>Corrupt Packets</th>
    </tr>
    <#list containers as container>
        <tr>
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
            <td align="left"><#list container.names() as name>${name}</#list></td>
            <td>
                <a href="/remove/${container.id()}"><i class="fa fa-trash fa-lg" style="color: red;"></i></a>

                <#if container.status()?contains("Paused")>
                    <a href="/unpause/${container.id()}"><i class="fa fa-play-circle fa-lg" style="color: green;"></i></a>
                <#elseif container.status()?contains("Up")>
                    <a href="/pause/${container.id()}"><i class="fa fa-pause-circle fa-lg" style="color: gold;"></i></a>
                    <a href="/stop/${container.id()}"><i class="fa fa-stop-circle fa-lg" style="color: gold;"></i></a>
                <#else>
                    <a href="/start/${container.id()}"><i class="fa fa-play-circle fa-lg" style="color: green;"></i></a>
                </#if>
            </td>
            <td>
                <#--                <a href="/unpause/${container.id()}"><i class="fa fa-cogs fa-lg" style="color: black;"></i></a> Delay=0ms, Drop=Y, Corrupt=Y-->
                <#if container.status()?contains("Up")>
                <!-- https://getbootstrap.com/docs/4.6/components/dropdowns/ -->
                    <#--                    <div class="input-group-prepend">-->
                    <#--                        <label class="input-group-text" for="inputGroupSelect01">Options</label>-->
                    <#--                    </div>-->
                    <#if delays[container.id()]??>
                        <#assign delay = delays[container.id()]>
                    <#else>
                        <#assign delay = 0>
                    </#if>
                    <select class="custom-select" id="delay_${container.id()}">
                        <option value="/delay/${container.id()}/0" <#if delay == 0>selected</#if>>0ms</option>
                        <option value="/delay/${container.id()}/10" <#if delay == 10>selected</#if>>10ms</option>
                        <option value="/delay/${container.id()}/100" <#if delay == 100>selected</#if>>100ms</option>
                        <option value="/delay/${container.id()}/500" <#if delay == 500>selected</#if>>500ms</option>
                        <option value="/delay/${container.id()}/1000" <#if delay == 1000>selected</#if>>1s</option>
                        <option value="/delay/${container.id()}/10000" <#if delay == 10000>selected</#if>>10s</option>
                    </select>
                </#if>
            </td>
            <td>
                <#if container.status()?contains("Up")>
                     <select class="custom-select" id="drop_${container.id()}">
                        <option value="#" <#if delay == 0>selected</#if>>TODO</option>
                     </select>
                </#if>
            </td>
            <td>
                <#if container.status()?contains("Up")>
                    <select class="custom-select" id="corrupt_${container.id()}">
                        <option value="#" <#if delay == 0>selected</#if>>TODO</option>
                    </select>
                </#if>
            </td>
        </tr>
    </#list>
</table>
</#if>

<#if operations?size == 0>
<#else>
<div class="card">
    <div class="card-header">
        History of operations
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
<script language="JavaScript" >
    $('select').on('change', function() {
        //alert( this.id + '=' + this.value );
        if (this.id.startsWith("delay_"))
        {
            window.location.replace(this.value);
        }
    });
</script>

</#macro>

<#include "common/footer.ftl">