AUI.add(
    'threads-monitor-util',
    function (A) {

        var INVOKER_URL = themeDisplay.getPathContext() + '/api/jsonws/invoke';

        var Util = {

            // переводы пагинатора
            getPaginatorLanguages: function () {
                console.log('util - getPaginatorLanguages!!!');
                var languages = {
                    'ru': {
                        'pagination': {
                            'page_size': Liferay.Language.get('page_size_pagination'),
                            'first': Liferay.Language.get('first_pagination'),
                            'first_title': Liferay.Language.get('first_title_pagination'),
                            'last': Liferay.Language.get('last_pagination'),
                            'last_title': Liferay.Language.get('last_title_pagination'),
                            'prev': Liferay.Language.get('prev_pagination'),
                            'prev_title': Liferay.Language.get('prev_title_pagination'),
                            'next': Liferay.Language.get('next_pagination'),
                            'next_title': Liferay.Language.get('next_title_pagination')
                        }
                    }
                };
                return languages;
            },

            // общая функция для вызова сервисов
            invokeService: function (payload, callback, sync) {
                var instance = this;

                callback = callback || {};

                for (var i in payload) {
                    if (!payload[i].serviceContext) {
                        payload[i].serviceContext = {
                            plid: themeDisplay.getPlid(),
                            scopeGroupId: themeDisplay.getScopeGroupId()
                        };
                    }
                }

                A.io.request(INVOKER_URL, {
                    cache: false,
                    sync: !!sync,
                    data: {
                        cmd: JSON.stringify(payload),
                        p_auth: Liferay.authToken,
                        doAsUserId: themeDisplay.getDoAsUserIdEncoded()
                    },
                    dataType: 'json',
                    on: {
                        start: callback.start,
                        failure: function (event) {
                            if (callback.failure) callback.failure.call(instance, event);
                        },

                        success: function (event) {
                            if (callback.success) {
                                var data = this.get('responseData');
                                callback.success.apply(this, [data, event]);
                            }
                        }
                    }
                });
            },

            // стандартное сообщения об успешном выполнении
            invokeSuccess: function () {
                this.showSuccessMessage(Liferay.Language.get('your-request-completed-successfully'));
            },

            // очистка сообщений
            clearMessages: function () {
                var alertContainer = A.one('.lfr-alert-container');
                if (alertContainer) alertContainer.html('');
            },

            // вывод сообщения об ошибке
            showErrorMessage: function (text, notClear) {
                if (!notClear) notClear = false;
                if (notClear == false) this.clearMessages();
                new Liferay.Alert({
                    delay: {
                        hide: 10000,
                        show: 0
                    },
                    duration: 400,
                    icon: 'exclamation-full',
                    message: text,
                    type: 'danger'
                }).render();
            },

            // вывод сообщения об успехе
            showSuccessMessage: function (text) {
                this.clearMessages();
                new Liferay.Alert({
                    delay: {
                        hide: 10000,
                        show: 0
                    },
                    duration: 400,
                    icon: 'check',
                    message: text,
                    type: 'success'
                }).render();
            }

        };

        Liferay.SnowReplicator.ThreadsMonitor.Util = Util;
    },
    '',
    {
        requires: ['aui-base']
    }
);