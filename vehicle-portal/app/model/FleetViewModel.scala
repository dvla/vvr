package model

import play.api.libs.json._
import uk.gov.dvla.domain.{FleetFilter, Vehicle}
import scala.collection.mutable.ListBuffer
import utils.URLBuilderUtils
import controllers.routes
import play.api.data.FormError
import forms.VehicleRefinedSearchForm


case class FleetViewModel(vehicles: List[VehicleListViewModel],
                          vehiclesFound: Int,
                          pageSize: Int,
                          pageNo: Int,
                          noOfPages: Int,
                          pages: Seq[Page],
                          previous: Page,
                          next: Page,
                          fleetNumber: String,
                          entriesLimitPages: Seq[Page],
                          params: FleetParams,
                          makes: String) {

  val filterFormUrl = routes.ViewFleetRecords.refinedSearchSubmit(pageNo)

  val exportUrl = URLBuilderUtils.buildUrl(routes.FileExportFleetRecords.exportFleet.url, params.data.toQueryMap)

  val alertFilter = params.data.filter.getOrElse(FleetFilter.ALL)

  val activeFilter = if (params.data.isRefined) FleetFilter.CUSTOM else alertFilter

  def alertFilterUrl(filter: String) = URLBuilderUtils.buildUrl(routes.ViewFleetRecords.viewFleet(1).url, VehicleRefinedSearchData(Some(filter)).toQueryMap)

  def consolidateSearchFilterErrors() = {
    params.form.errors(VehicleRefinedSearchForm.REGISTRATION_PARAM).take(1) ++
    params.form.errors(VehicleRefinedSearchForm.MAKE_PARAM).take(1) ++
    (if (!FleetFieldViewModel.isDateRangeFieldValid(params.form(VehicleRefinedSearchForm.MOT_PARAM)))
      Seq(FormError(VehicleRefinedSearchForm.MOT_PARAM, "vvrform.enter.a.valid.date"))
    else Nil) ++
    params.form.errors(VehicleRefinedSearchForm.MOT_PARAM).take(1)
  }

  def fieldViewModel(fieldName: String, message: String, checkboxes: List[String] = Nil) =
    new FleetFieldViewModel(params.form(fieldName), message, checkboxes)

}

object FleetViewModel {

  def apply(json: JsValue, pageOffset: Int, pageSize: Int, fleetNumber: String,
            fleetParams: FleetParams, makes:String): FleetViewModel = {

    val fleetSize = (json \ "found").as[Int]
    val pageNo: Int = (pageOffset / pageSize) + 1
    val noOfPages: Int = Math.ceil(1.0 * fleetSize / pageSize).toInt
    val pages = buildPagination(fleetNumber, noOfPages, pageSize, pageOffset, fleetParams.data)
    val previous = buildPreviousPage(fleetNumber, pageSize, pageOffset, fleetParams.data)
    val next = buildNextPage(fleetNumber, pageSize, pageOffset, fleetSize, fleetParams.data)
    val entriesLimitPages = buildEntriesLinkPages(pageSize, fleetParams.data)
    (json \ "vehicles") match {
      case JsArray(ar) =>
        val vehicles = ar.map {
          vehicle => VehicleListViewModel(Vehicle(vehicle))
        }
        FleetViewModel(vehicles.toList, fleetSize, pageSize, pageNo, noOfPages, pages, previous, next, fleetNumber, entriesLimitPages, fleetParams, makes)
      case _ =>
        FleetViewModel(Nil, 0, pageSize, pageNo, noOfPages, pages, previous, next, fleetNumber, entriesLimitPages, fleetParams, makes)
    }
  }

  def apply(json: JsValue): Seq[VehicleListExportViewModel] = (json \ "vehicles") match {
    case JsArray(ar) =>
      ar.map {
        vehicle => VehicleListExportViewModel(Vehicle(vehicle))
      }
    case _ =>
      Nil
  }

  private def buildPagination(fleetNumber: String, noOfPages: Int, limit: Int, currentOffset: Int, searchFilter: VehicleRefinedSearchData): Seq[Page] = {
    val pages = new ListBuffer[Page]
    val selectedPageNumber: Int = (currentOffset / limit) + 1
    val firstPage = 1
    val lastPage = noOfPages.max(1)

    pages.append(new Page("page.first", URLBuilderUtils.buildUrl(routes.ViewFleetRecords.viewFleet(firstPage).url, searchFilter.toQueryMap), selectedPageNumber == firstPage, false))
    pages.append(new Page("page.last", URLBuilderUtils.buildUrl(routes.ViewFleetRecords.viewFleet(lastPage).url, searchFilter.toQueryMap), selectedPageNumber == lastPage, false))

    pages
  }

  private def buildPreviousPage(fleetNumber: String, limit: Int, currentOffset: Int, searchFilter: VehicleRefinedSearchData): Page = {
    val prevPage = if (currentOffset == 0) 1 else 1 + (currentOffset - limit) / limit
    val url = URLBuilderUtils.buildUrl(routes.ViewFleetRecords.viewFleet(prevPage).url, searchFilter.toQueryMap)
    Page("page.previous", url, false, currentOffset != 0)
  }

  private def buildNextPage(fleetNumber: String, limit: Int, currentOffset: Int, fleetSize: Int, searchFilter: VehicleRefinedSearchData): Page = {
    val nextPage = 1 + (currentOffset + limit) / limit
    val url = URLBuilderUtils.buildUrl(routes.ViewFleetRecords.viewFleet(nextPage).url, searchFilter.toQueryMap)
    Page("page.next", url, false, (currentOffset + limit) < fleetSize)
  }

  def validatePageOffset(offset: Int, limit: Int, maxOffset: Int) = {
    if (offset < 0 || offset > maxOffset || offset % limit > 0) {
      0
    } else {
      offset
    }
  }

  def buildEntriesLinkPages(pageSize: Int, searchFilter: VehicleRefinedSearchData): Seq[Page] = {
    val pages = new ListBuffer[Page]
    val max = 100

    for (count <- 25 to max by 25) {
      pages.append(new Page(count.toString, URLBuilderUtils.buildUrl(routes.ViewFleetRecords.selectPageSize(count).url, searchFilter.toQueryMap), count == pageSize, false))
    }
    pages
  }

}

case class Page(pageName: String, pageUrl: String, selectedPage: Boolean, showPage: Boolean)
