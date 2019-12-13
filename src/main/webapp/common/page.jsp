<%--
  Created by IntelliJ IDEA.
  User: wzy
  Date: 2019/12/4
  Time: 9:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script id="paginateTemplate" type="x-tmpl-mustache">
<div class="col-xs-6">
    <div class="dataTables_info" id="dynamic-table_info" role="status" aria-live="polite">
        总共 {{total}} 中的 {{from}} ~ {{to}}
    </div>
</div>

<div class="col-xs-6">
    <div class="dataTables_paginate paging_simple_numbers" id="dynamic-table_paginate">
        <ul class="pagination">
            <li class="paginate_button previous {{^firstUrl}}disabled{{/firstUrl}}" aria-controls="dynamic-table" tabindex="0">
                <a href="#" data-target="1" data-url="{{firstUrl}}" class="page-action">首页</a>
            </li>
            <li class="paginate_button {{^beforeUrl}}disabled{{/beforeUrl}}" aria-controls="dynamic-table" tabindex="0">
                <a href="#" data-target="{{beforePageNo}}" data-url="{{beforeUrl}}" class="page-action">前一页</a>
            </li>
            <li class="paginate_button active" aria-controls="dynamic-table" tabindex="0">
                <a href="#" data-id="{{pageNo}}" >第{{pageNo}}页</a>
                <input type="hidden" class="pageNo" value="{{pageNo}}" />
            </li>
            <li class="paginate_button {{^nextUrl}}disabled{{/nextUrl}}" aria-controls="dynamic-table" tabindex="0">
                <a href="#" data-target="{{nextPageNo}}" data-url="{{nextUrl}}" class="page-action">后一页</a>
            </li>
            <li class="paginate_button next {{^lastUrl}}disabled{{/lastUrl}}" aria-controls="dynamic-table" tabindex="0">
                <a href="#" data-target="{{maxPageNo}}" data-url="{{lastUrl}}" class="page-action">尾页</a>
            </li>
        </ul>
    </div>
</div>
</script>

<script type="text/javascript">
    var paginateTemplate = $("#paginateTemplate").html();
    Mustache.parse(paginateTemplate);

    /**
     * 分页插件渲染方法
     * @param url url
     * @param total 总共多少条
     * @param pageNo 第几页
     * @param pageSize 每页行数
     * @param currentSize 当前这一页有多少条
     * @param idElement 放到页面那个元素里
     * @param callback 回调方法
     */
    function renderPage(url, total, pageNo, pageSize, currentSize, idElement, callback) {
        //最大页数
        var maxPageNo = Math.ceil(total / pageSize);
        //判断是否有参数
        var paramStrChar = url.indexOf("?") > 0 ? "&" : "?";
        //比如第1页，(1 - 1) * 10 + 1, from就是1开始
        var from = (pageNo - 1) * pageSize + 1;
        var view = {
            //如果起点大于总数的话，就等于total，否则就等于from
            from: from > total ? total : from,
            //如果from + 当前页的记录条数 - 1 大于总数那么就等于总数，否则就是默认计算的结果
            to:(from + currentSize - 1) > total? total: (from + currentSize - 1),
            //记录总数
            total: total,
            //当前第几页
            pageNo: pageNo,
            //最大页数
            maxPageNo: maxPageNo,
            //下一页
            nextPageNo: pageNo >= maxPageNo ? maxPageNo: (pageNo + 1),
            //上一页
            beforePageNo: pageNo == 1 ? 1 :(pageNo - 1),
            //首页url
            firstUrl: (pageNo == 1) ? '' : (url + paramStrChar + "pageNo=1&pageSize=" + pageSize),
            //上一页url：主要计算两个参数，页码和每页多少条记录
            beforeUrl: (pageNo == 1)? '': (url + paramStrChar + "pageNo=" + (pageNo - 1) + "&pageSize=" + pageSize),
            //下一页url
            nextUrl: (pageNo >= maxPageNo) ? '' : (url + paramStrChar + "pageNo=" + (pageNo + 1) + "&pageSize=" + pageSize),
            //最后一页url
            lastUrl: (pageNo >= maxPageNo) ? '' : (url +paramStrChar + "pageNo=" + maxPageNo + "&pageSize=" + pageSize)
        };

        $("#" + idElement).html(Mustache.render(paginateTemplate, view));
        //page-action用来匹配点击事件
        $(".page-action").click(function (e) {
            //拦截默认事件
            e.preventDefault();
            //修改页数
            $("#" + idElement + " .pageNo").val($(this).attr("data-target"));
            //目标url
            var targetUrl = $(this).attr("data-url");
            //如果不为空，因为最后一页或者第一页按钮可能为空
            if (targetUrl != '') {
                //发送请求获取数据，之后成功后执行回调方法
                $.ajax({
                    url: targetUrl,
                    sucess: function(result) {
                        if (callback) {
                            callback(result, url);
                        }
                    }
                })
            }
        })
    }
</script>