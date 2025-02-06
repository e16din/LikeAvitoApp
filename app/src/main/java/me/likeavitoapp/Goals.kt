package me.likeavitoapp

// NOTE: to demonstrate goals

fun Any.contains(body: () -> Unit) {}
fun values(body: () -> Unit) {}
fun openScreen(screen: String) {}

fun Goals() {
    "Получили товар".contains {
        "Заказали товар".contains {
            "Выбрали товар".contains {
                "Получили список подходящих товаров".contains {
                    "Задали описание нужного товара".contains {
                        values {
                            "категорию"
                            "название"
                            "вилку цены"
                            "регион"
                        }

                        openScreen("Страница поиска").contains {
                            openScreen("Главный экран")
                        }
                    }
                }
                "Посмотрели подробности о товаре".contains {
                    values {
                        "название"
                        "цену"
                        "фото"
                        "описание"

                        "фото продавца"
                        "имя продавца"
                    }
                    openScreen("Детали товара")
                }
            }
            "Оплатили товар".contains {
                openScreen("Оплата товара")
            }
        }
        "Получили уведомление что товар можно забирать".contains {
            openScreen("Экран заказа").contains {
                openScreen("Список заказов").contains {
                    openScreen("Главный экран")
                }
            }
            openScreen("Страница уведомлений").contains {
                openScreen("Главный экран")
            }
        }
    }

    "Получили деньги".contains {
        "Продали товар".contains {
            "Оформили профиль".contains {
                "Задали".contains {
                    values {
                        "фото"
                        "ФИО"
                        "регион"
                    }

                    openScreen("Профиль продавца").contains {
                        openScreen("Главный экран")
                    }
                }
            }
            "Подали объявление".contains {
                "Задали".contains {
                    values {
                        "категорию"
                        "название"
                        "вилку цены"
                        "регион"
                        "фото"
                        "описание"
                    }

                    openScreen("Подача объявления").contains {
                        openScreen("Главный экран")
                    }
                }
                ""
            }
            "Одобрили предложение от покупателя".contains {
                "Получили предложение от покупателя".contains {
                    openScreen("Свой товар с переговорами о покупке").contains {
                        openScreen("Список своих товаров").contains {
                            openScreen("Главный экран")
                        }
                    }
                    "Получили уведомление о предложении".contains {
                        openScreen("Главный экран")
                    }
                }
            }
        }
    }
}