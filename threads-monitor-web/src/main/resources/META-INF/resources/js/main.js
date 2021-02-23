AUI.add(
    'threads-monitor-main-js',
    function (A) {

        var Lang = A.Lang;

        var isBoolean = Lang.isBoolean;
        var isFunction = Lang.isFunction;
        var isNumber = Lang.isNumber;

        var STR_BLANK = '';

        var SAVE_SORT_TIMEOUT  = 200; // задержка сохранения сортировки, мс (чтобы не долбиться в базу слишком часто)
        var SAVE_WIDTH_TIMEOUT = 200; // задержка сохранения ширины, мс (чтобы не долбиться в базу слишком часто)
        var SAVE_ORDER_TIMEOUT = 200; // задержка сохранения порядка столбцов, мс (чтобы не долбиться в базу слишком часто)
        var SAVE_PAGE_SIZE_TIMEOUT = 200; // задержка сохранения кол-ва записей на странце, мс (чтобы не долбиться в базу слишком часто)

        var PAGINATION_SIZE_SELECTOR = [5, 10, 25, 50, 75, 100]; // кол-во записей для выбора в пагинаторе

        var toInt = function (value) {
            return Lang.toInt(value, 10, 0);
        };

        var Util = Liferay.SnowReplicator.ThreadsMonitor.Util;

        var MainJS = A.Component.create(
            {
                ATTRS: {

                    id: {
                        value: STR_BLANK
                    },

                    p_p_auth: {
                        value: STR_BLANK
                    },

                    namespace: {
                        value: STR_BLANK
                    },

                    portletId: {
                        value: STR_BLANK
                    },

                    groupId: {
                        setter: toInt,
                        validator: isNumber
                    },

                    threadsMonitorDataJsonString: {
                        value: STR_BLANK
                    },

                    columnMinWidth: {
                        setter: toInt,
                        validator: isNumber
                    }

                },

                NAME: 'mainJS',

                constructor: function (config) {
                    var id = config.id;
                    config.contentBox = config.contentBox || '#' + id;
                    MainJS.superclass.constructor.apply(this, arguments);
                },

                get: function (id) {
                    var instance = this;
                    var mainJS = null;
                    if (instance._cache[id]) {
                        mainJS = instance._cache[id];
                    } else {
                        console.error('main.js - get object mainJS not found (id: ' + id + ')');
                    }
                    return mainJS;
                },

                prototype: {
                    initializer: function () {
                        var instance = this;
                        instance.bindUI();
                        MainJS.register(instance);
                        instance.afterLoad();
                    },

                    bindUI: function () {
                        var instance = this;
                        var contentBox = instance.get('contentBox');
                        var namespace = instance.get('namespace');

                        contentBox.delegate('click',    instance._onClickLoadThreadsData,        '.load-threads-data-btn',             instance);
                    },

                    // действия после загрузки
                    afterLoad: function () {
                        var instance = this;
                        var namespace = instance.get('namespace');
                        var columnMinWidth = instance.get('columnMinWidth');

                        var threadsMonitorData = instance.parseThreadsMonitorDataFromJsonString(instance.get('threadsMonitorDataJsonString'));
                        if (!threadsMonitorData) return;

                        var threadsMonitorDataConfiguration = {
                            tabulatorPlace: namespace + 'tabulatorPlace',
                            columns:                threadsMonitorData.columnsData.columns,
                            initialSort:            threadsMonitorData.columnsData.initialSort,
                            tableData:              threadsMonitorData.threadsData.length > 0 ? threadsMonitorData.threadsData : new Array(),
                            columnMinWidth:         columnMinWidth,
                            movableColumns:         true,
                            pagination:             'local',
                            paginationSize:         threadsMonitorData.pageSize,
                            paginationSizeSelector: PAGINATION_SIZE_SELECTOR,
                            languageId:             threadsMonitorData.languageId,
                            langs:                  threadsMonitorData.langs
                        };

                        instance.drawTabulator(threadsMonitorDataConfiguration);
                    },

                    // нарисовать табулятор
                    drawTabulator: function (threadsMonitorDataConfiguration) {
                        var instance = this;
                        console.log('drawTabulator - threadsMonitorDataConfiguration = ' + JSON.stringify(threadsMonitorDataConfiguration));

                        Liferay.SnowReplicator.ThreadsMonitor.Tabulator(function (Tabulator) {
                            var threadsMonitorDataTable = new Tabulator('#' + threadsMonitorDataConfiguration.tabulatorPlace, {
                                height:                 threadsMonitorDataConfiguration.height,
                                data:                   threadsMonitorDataConfiguration.tableData,
                                columns:                threadsMonitorDataConfiguration.columns,
                                initialSort:            threadsMonitorDataConfiguration.initialSort,
                                movableColumns:         threadsMonitorDataConfiguration.movableColumns,
                                columnMinWidth:         threadsMonitorDataConfiguration.columnMinWidth,
                                pagination:             threadsMonitorDataConfiguration.pagination,
                                paginationSize:         threadsMonitorDataConfiguration.paginationSize,
                                paginationSizeSelector: threadsMonitorDataConfiguration.paginationSizeSelector,
                                langs:                  threadsMonitorDataConfiguration.langs,
                                dataSorted: function (sorters, rows) {
                                    instance.dataSorted(sorters, rows);
                                },
                                columnResized: function (column) {
                                    instance.columnResized (column);
                                },
                                columnMoved: function (column, columns) {
                                    instance.columnMoved(column, columns);
                                },
                                pageLoaded: function (pageno) {
                                    instance.pageLoaded(this);
                                }
                            });

                            threadsMonitorDataTable.setLocale(threadsMonitorDataConfiguration.languageId);
                            instance.set('threadsMonitorDataTable', threadsMonitorDataTable);
                        });
                    },

                    // функция срабатывающая при изменении сортировки столбца
                    dataSorted: function (sorters, rows) {
                        var instance = this;
                        for (var i=0; i < sorters.length; i++) {
                            var sorter = sorters[i];
                            if (sorter) {
                                if (instance.saveColumnSortDirTimeOut) clearTimeout(this.saveColumnSortDirTimeOut);
                                instance.saveColumnSortDirTimeOut = setTimeout(instance.saveColumnSortDir, SAVE_SORT_TIMEOUT, sorter.field, sorter.dir);
                            }
                        }
                    },

                    // сохранить настройку сортировки для указанного столбца
                    saveColumnSortDir: function (columnId, dir) {
                        var data = {};
                        data['/thread.threadtableprops/save-column-sort-dir'] = { columnId: columnId, dir: dir };

                        Util.invokeService(data, {
                            failure: function(event) {
                            },
                            success: function() {
                            }
                        });
                    },

                    // функция срабатывающая при изменении ширины столбца
                    columnResized: function (column) {
                        var instance = this;
                        if (column) {
                            if (instance.saveColumnWidthTimeOut) clearTimeout(this.saveColumnWidthTimeOut);
                            instance.saveColumnWidthTimeOut = setTimeout(instance.saveColumnWidth, SAVE_WIDTH_TIMEOUT, column.getField(), column.getWidth());
                        }
                    },

                    // сохранить настройку ширины для указанного столбца
                    saveColumnWidth: function (columnId, width) {
                        var data = {};
                        data['/thread.threadtableprops/save-column-width'] = { columnId: columnId, width: width };

                        Util.invokeService(data, {
                            failure: function(event) {
                            },
                            success: function() {
                            }
                        });
                    },

                    // функция срабатывающая при изменении порядка столбцов таблицы
                    columnMoved: function (column, columns) {
                        var instance = this;
                        var newColumnOrder = new Array();
                        for (var i=0; i < columns.length; i++) {
                            newColumnOrder.push(columns[i].getField());
                        }
                        if (instance.saveColumnOrderTimeOut) clearTimeout(this.saveColumnOrderTimeOut);
                        instance.saveColumnOrderTimeOut = setTimeout(instance.saveColumnsOrder, SAVE_ORDER_TIMEOUT, newColumnOrder.join());
                    },

                    // сохранить настройку указывающую в каком порядке должны следовать друг за другом столбцы табулятора
                    saveColumnsOrder: function (columnOrder) {
                        var data = {};
                        data['/thread.threadtableprops/save-columns-order'] = { columnsOrder: columnOrder };

                        Util.invokeService(data, {
                            failure: function(event) {
                            },
                            success: function() {
                            }
                        });
                    },

                    // функция срабатывающая при окончании загрузки страницы
                    pageLoaded: function (tabulator) {
                        var instance = this;
                        var pageSize = tabulator.getPageSize();
                        if (instance.savePageSizeTimeOut) clearTimeout(this.savePageSizeTimeOut);
                        instance.savePageSizeTimeOut = setTimeout(instance.savePageSize, SAVE_PAGE_SIZE_TIMEOUT, pageSize);
                    },

                    // сохранить настройку указывающую сколько записей показывать в таблице
                    savePageSize: function (pageSize) {
                        var data = {};
                        data['/thread.threadtablesettings/save-page-size'] = { pageSize: pageSize };

                        Util.invokeService(data, {
                            failure: function(event) {
                            },
                            success: function() {
                            }
                        });
                    },

                    // получить данные по потокам из сериализованной json строки
                    parseThreadsMonitorDataFromJsonString: function (threadsMonitorDataJsonString) {
                        try {
                            var threadsMonitorData = JSON.parse(threadsMonitorDataJsonString);
                            return threadsMonitorData;
                        }
                        catch (e) {
                            console.error('main.js - parseThreadsMonitorDataFromJsonString() - parse exception: ' + e.name + ' ' + e.message);
                            console.error('No threads data available');
                        }
                        return null;
                    },

                    _onClickLoadThreadsData: function (event) {
                        console.log('_onClickLoadThreadsData');
                        var instance = this;
                        instance.loadThreadsData();
                    },

                    // получить список процессов с сервера
                    loadThreadsData: function () {
                        console.log('loadThreadsData');
                        var instance = this;
                        var url = Liferay.PortletURL.createResourceURL();
                        url.setPortletId(instance.get('portletId'));
                        url.setResourceId('/threads-monitor-portlet/load-threads-data');
                        url.setDoAsUserId(Liferay.ThemeDisplay.getDoAsUserIdEncoded());
                        url.setParameter('p_p_auth', instance.get('p_p_auth'));

                        var data = {};

                        A.io.request(url.toString(), {
                            dataType: 'json',
                            data: data,
                            on: {
                                success: function (event) {
                                    var responseData = this.get('responseData');
                                    console.log('loadThreadsData - responseData = ' + JSON.stringify(responseData));
                                    instance.replaceThreadsTabulatorData(responseData);
                                }
                            }
                        });
                    },

                    // переустановка новых данных в табулятор
                    replaceThreadsTabulatorData: function (tableData) {
                        console.log('replaceThreadsTabulatorData - tableData = ' + JSON.stringify(tableData));
                        var instance = this;
                        var threadsMonitorDataTable = instance.get('threadsMonitorDataTable');
                        threadsMonitorDataTable.replaceData(tableData);
                    },

                    // Получить строку табулятора
                    getThreadsTabulatorRow: function (id) {
                        var instance = this;
                        var tabulator = instance.getThreadsTabulator();
                        if (tabulator) {
                            var rows = tabulator.getRows();
                            for (var i = 0; i < rows.length; i++) {
                                if (rows[i].getData().id == id) {
                                    return rows[i];
                                }
                            }
                        }
                        return null;
                    }
                },

                register: function (obj) {
                    var instance = this;
                    var id = obj.get('id');
                    instance._cache[id] = obj;
                    Liferay.fire(
                        'mainJS:registered',
                        {
                            mainJS: obj
                        }
                    );
                },

                _cache: {}
            }
        );

        Liferay.SnowReplicator.ThreadsMonitor.MainJS = MainJS;
    },
    '',
    {
        requires: ['aui-base', 'aui-component', 'aui-io-request', 'liferay-alert', 'liferay-portlet-url', 'threads-monitor-util']
    }
);