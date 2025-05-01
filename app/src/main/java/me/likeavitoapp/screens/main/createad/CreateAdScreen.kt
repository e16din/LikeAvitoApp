package me.likeavitoapp.screens.main.createad

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.likeavitoapp.R
import me.likeavitoapp.developer.primitives.work
import me.likeavitoapp.get
import me.likeavitoapp.model.CreateAdRequest
import me.likeavitoapp.model.IScreen
import me.likeavitoapp.model.ScreensNavigator
import me.likeavitoapp.model.UpdatableState
import me.likeavitoapp.recordScenarioStep
import me.likeavitoapp.screens.main.createad.steps.CategoryStepScreen
import me.likeavitoapp.screens.main.createad.steps.DeliveryStepScreen
import me.likeavitoapp.screens.main.createad.steps.DescriptionStepScreen
import me.likeavitoapp.screens.main.createad.steps.FinalStepScreen


enum class CreateAdStep(val label: String) {
    Description("Описание"),
    Category("Категория"),
    PickupPoints("Пункты выдачи"),
    Additions("Дополнения"),
}

class CreateAdScreen(
    val navigator: ScreensNavigator
) : IScreen {

    class State {
        val activeStep = UpdatableState<CreateAdStep?>(null)
        val steps = UpdatableState(CreateAdStep.entries)
        val errorStepIndex = UpdatableState<Int?>(null)
        val doneEnabled = UpdatableState(false)
    }

    val state = State()
    val stepsNavigator = ScreensNavigator()

    init {
        if (get.sources().app.activeCreateAdRequest == null) {
            get.sources().app.activeCreateAdRequest = CreateAdRequest()
        }

        selectStep(CreateAdStep.Description)
    }

    fun StartScreenUseCase() {
        recordScenarioStep()

    }

    fun PressBackUseCase() {
        recordScenarioStep()

        get.sources().app.mainScreen.returnToActiveTab()
    }

    fun ClickToDoneUseCase() {
        recordScenarioStep()

        with(get.sources().app.activeCreateAdRequest!!) {
            if (
                checkIsValid(CreateAdStep.Description)
                && checkIsValid(CreateAdStep.Category)
                && checkIsValid(CreateAdStep.PickupPoints)
                && checkIsValid(CreateAdStep.Additions)
            ) {
                if (price == 0) {
                    createAd()

                } else {
                    get.sources().app.pay { success ->
                        createAd()
                    }
                }
            }
        }
    }

    fun ClickToCreateAdUseCase() {
        recordScenarioStep()

        ClickToDoneUseCase()
    }

    fun ClickToStepUseCase(step: CreateAdStep) {
        recordScenarioStep(step)

        val steps = state.steps.value
        val index = steps.indexOf(step)
        val isPreviousStep = index < steps.indexOf(state.activeStep.value)

        if (isPreviousStep) {
            selectStep(step)

        } else {
            if (checkIsValid(state.activeStep.value!!)) {
                selectStep(step)
            }
        }

        state.doneEnabled.next(index == state.steps.value.size - 1)
    }

    private fun checkIsValid(step: CreateAdStep = state.activeStep.value!!): Boolean {
        var fieldName: String? = null
        with(get.sources().app.activeCreateAdRequest!!) {
            when (step) {
                CreateAdStep.Description -> {
                    fieldName = if (title.isNullOrEmpty()) {
                        get.sources().platform.getString(R.string.title_arg)
                    } else if (description.isNullOrEmpty()) {
                        get.sources().platform.getString(R.string.description_arg)
                    } else if (photos.isEmpty()) {
                        get.sources().platform.getString(R.string.photo_arg)
                    } else {
                        null
                    }
                }

                CreateAdStep.Category -> {
                    if (categoryId == null) {
                        fieldName = get.sources().platform.getString(R.string.category_arg)
                    }
                }

                CreateAdStep.PickupPoints -> {
                    val ownerAddressId = 0
                    fieldName = if (selectedPickupPointTypes.isEmpty()) {
                        get.sources().platform.getString(R.string.possible_pickup_points_arg)
                    } else if (selectedPickupPointTypes.contains(ownerAddressId) && address.isNullOrEmpty()) {
                        get.sources().platform.getString(R.string.address_arg)
                    } else {
                        null
                    }
                }

                CreateAdStep.Additions -> {

                }
            }
        }

        fieldName?.let {
            state.errorStepIndex.next(
                state.steps.value.indexOf(step)
            )

            get.sources().app.message.next(
                get.sources().platform.getString(R.string.fill_the_field_message, fieldName)
            )
            return false
        }

        state.errorStepIndex.next(null)

        return true
    }

    private fun createAd() {
        get.sources().app.loading.next(true)
        work {
            val result = get.sources().backend.adsService.createAd(
                get.sources().app.activeCreateAdRequest!!
            )
            withContext(Dispatchers.Main) {
                get.sources().app.loading.next(false)
                if (result.getOrNull() == true) {

                    get.sources().app.message.next(
                        get.sources().platform.getString(R.string.create_ad_success_message)
                    )
                }
            }
        }
    }

    private fun selectStep(step: CreateAdStep) {
        state.activeStep.next(step)

        stepsNavigator.startScreen(
            when (step) {
                CreateAdStep.Description -> DescriptionStepScreen(stepsNavigator)
                CreateAdStep.Category -> CategoryStepScreen(stepsNavigator)
                CreateAdStep.PickupPoints -> DeliveryStepScreen(stepsNavigator)
                CreateAdStep.Additions -> FinalStepScreen(stepsNavigator)
            },
            fromScreens = true
        )
    }

}

