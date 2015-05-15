package org.optaconf.service;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.optaconf.bridge.devoxx.DevoxxImporter;
import org.optaconf.cdi.ScheduleManager;
import org.optaconf.domain.Schedule;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/{conferenceId}/schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScheduleService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScheduleService.class);

    @Inject
    private ScheduleManager scheduleManager;

    @Inject
    private DevoxxImporter devoxxImporter;

    @Inject
    private SolverFactory solverFactory;

    @Resource(name = "DefaultManagedExecutorService")
    private ManagedExecutorService executor;

    @POST
    @Path("/import/devoxx")
    public String importDevoxx(@PathParam("conferenceId") Long conferenceId) {
        Schedule schedule = devoxxImporter.importSchedule();
        scheduleManager.setSchedule(schedule);
        return "Devoxx schedule with " + schedule.getDayList().size() + " days, "
                + schedule.getTimeslotList().size() + " timeslots, "
                + schedule.getRoomList().size() + " rooms, "
                + schedule.getTrackList().size() + " tracks, "
                + schedule.getSpeakerList().size() + " speakers, "
                + schedule.getTalkList().size() + " talks imported successfully.";
    }

    @PUT
    @Path("/solve")
    public String solveSchedule(@PathParam("conferenceId") Long conferenceId) {
        Solver oldSolver = scheduleManager.getSolver();
        if (oldSolver != null && oldSolver.isSolving()) {
            oldSolver.terminateEarly();
        }
        Solver solver = solverFactory.buildSolver();
        // TODO Use async solving https://developer.jboss.org/message/910391
//        scheduleManager.setSolver(solver);
//        executor.submit(new SolverCallable(solver, scheduleManager.getSchedule()));
//        return "Solved started.";
        solver.solve(scheduleManager.getSchedule());
        scheduleManager.setSchedule((Schedule) solver.getBestSolution());
        return "Solved. Sorry it took so long";
    }

    @GET
    @Path("/isSolving")
    public boolean isSolving(@PathParam("conferenceId") Long conferenceId) {
        Solver solver = scheduleManager.getSolver();
        return solver != null && solver.isSolving();
    }

    @PUT
    @Path("/terminateSolving")
    public void terminateSolving(@PathParam("conferenceId") Long conferenceId) {
        Solver solver = scheduleManager.getSolver();
        if (solver != null) {
            solver.terminateEarly();
        }
    }

    private class SolverCallable implements Runnable {

        private final Solver solver;
        private final Schedule schedule;

        private SolverCallable(Solver solver, Schedule schedule) {
            this.solver = solver;
            this.schedule = schedule;
        }

        public void run() {
            solver.addEventListener(new SolverEventListener() {
                @Override
                public void bestSolutionChanged(BestSolutionChangedEvent bestSolutionChangedEvent) {
                    scheduleManager.setSchedule((Schedule) bestSolutionChangedEvent.getNewBestSolution()); // TODO throws eaten Exception
                }
            });
            solver.solve(schedule);
            Schedule bestSchedule = (Schedule) solver.getBestSolution(); // TODO throws eaten Exception
            scheduleManager.setSchedule(bestSchedule);
            scheduleManager.setSolver(null);
        }
    }

}
