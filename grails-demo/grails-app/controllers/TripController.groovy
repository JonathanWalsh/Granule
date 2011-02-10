            
class TripController {
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ tripList: Trip.list( params ) ]
    }

    def show = {
        def trip = Trip.get( params.id )

        if(!trip) {
            flash.message = "Trip not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ trip : trip ] }
    }

    def delete = {
        def trip = Trip.get( params.id )
        if(trip) {
            trip.delete()
            flash.message = "Trip ${params.id} deleted"
            redirect(action:list)
        }
        else {
            flash.message = "Trip not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def trip = Trip.get( params.id )

        if(!trip) {
            flash.message = "Trip not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ trip : trip ]
        }
    }

    def update = {
        def trip = Trip.get( params.id )
        if(trip) {
            trip.properties = params
            if(!trip.hasErrors() && trip.save()) {
                flash.message = "Trip ${params.id} updated"
                redirect(action:show,id:trip.id)
            }
            else {
                render(view:'edit',model:[trip:trip])
            }
        }
        else {
            flash.message = "Trip not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def trip = new Trip()
        trip.properties = params
        return ['trip':trip]
    }

    def save = {
        def trip = new Trip(params)
        if(!trip.hasErrors() && trip.save()) {
            flash.message = "Trip ${trip.id} created"
            redirect(action:show,id:trip.id)
        }
        else {
            render(view:'create',model:[trip:trip])
        }
    }
}