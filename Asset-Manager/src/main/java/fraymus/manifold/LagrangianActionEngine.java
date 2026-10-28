package fraymus.manifold;

import java.util.Random;

/**
 * TELEOLOGICAL TIMELINE COLLAPSE (Section XXII — Principle of Least Action)
 *
 * Standard AI predicts the future from the past. Fraynix pulls the present
 * toward a desired future using Lagrangian Mechanics.
 *
 * Lagrangian of the Neural Manifold:
 *   L(q, q̇, t) = T(q̇) − V(q)
 *   where T = kinetic information (processing power)
 *             V = potential free energy (uncertainty/hunger)
 *
 * Action integral:
 *   S = ∫_{t1}^{t2} L(q, q̇, t) dt
 *
 * The system finds the path q*(t) that minimises S — by deriving the
 * Euler-Lagrange equation:
 *   d/dt (∂L/∂q̇) − ∂L/∂q = 0
 *
 * This gives the optimal next-action vector without exhaustive lookahead.
 * Fraynix does not predict the future — it computes the unique trajectory
 * that must lead there.
 */
public class LagrangianActionEngine {

    public static final int    DEFAULT_DIM     = 64;
    public static final int    DEFAULT_STEPS   = 32;
    public static final double DEFAULT_DT      = 0.05;
    public static final double DEFAULT_MASS    = 1.0;
    public static final double DEFAULT_DAMPING = 0.01;

    private final int    dim;
    private final int    integrationSteps;
    private final double dt;
    private final double mass;
    private final double damping;

    private double[] q;
    private double[] qDot;
    private double[] qGoal;

    private double kineticEnergy;
    private double potentialEnergy;
    private double lagrangian;
    private double actionIntegral;

    private long   stepCount;
    private long   goalUpdates;
    private long   trajectoryComputes;

    private final Random rng;

    public LagrangianActionEngine(int dim, int integrationSteps, double dt) {
        this.dim              = dim;
        this.integrationSteps = integrationSteps;
        this.dt               = dt;
        this.mass             = DEFAULT_MASS;
        this.damping          = DEFAULT_DAMPING;
        this.rng              = new Random(29L);

        this.q     = new double[dim];
        this.qDot  = new double[dim];
        this.qGoal = new double[dim];

        double scale = Math.sqrt(2.0 / dim);
        for (int i = 0; i < dim; i++) {
            q[i]     = rng.nextGaussian() * scale;
            qDot[i]  = rng.nextGaussian() * scale * 0.1;
            qGoal[i] = rng.nextGaussian() * scale;
        }
    }

    public LagrangianActionEngine(int dim) {
        this(dim, DEFAULT_STEPS, DEFAULT_DT);
    }

    public LagrangianActionEngine() {
        this(DEFAULT_DIM, DEFAULT_STEPS, DEFAULT_DT);
    }

    // ─── LAGRANGIAN COMPONENTS ───────────────────────────────────────────────

    /**
     * Kinetic energy T(q̇) = ½ m |q̇|²
     * Represents cognitive processing power / rate of learning.
     */
    public double kineticEnergy(double[] velocity) {
        double t = 0.0;
        for (double v : velocity) t += v * v;
        return 0.5 * mass * t;
    }

    /**
     * Potential energy V(q) = ½ |q − q_goal|²
     * Represents variational free energy / uncertainty / distance from goal.
     * Minimising V = minimising Free Energy = closing the gap to the goal.
     */
    public double potentialEnergy(double[] state) {
        double v = 0.0;
        for (int i = 0; i < dim; i++) {
            double d = state[i] - qGoal[i];
            v += d * d;
        }
        return 0.5 * v;
    }

    /**
     * L(q, q̇, t) = T(q̇) − V(q)
     */
    public double lagrangian(double[] state, double[] velocity) {
        return kineticEnergy(velocity) - potentialEnergy(state);
    }

    // ─── EULER-LAGRANGE EQUATION ─────────────────────────────────────────────

    /**
     * ∂L/∂q = −∂V/∂q = −(q − q_goal)
     * d/dt(∂L/∂q̇) = m·q̈
     *
     * Euler-Lagrange → m·q̈ = −(q − q_goal) − damping·q̇
     * This is a damped harmonic oscillator toward q_goal.
     * The acceleration IS the optimal next action.
     *
     * @return acceleration vector (optimal action)
     */
    public double[] eulerLagrangeAcceleration(double[] state, double[] velocity) {
        double[] acc = new double[dim];
        for (int i = 0; i < dim; i++) {
            double dVdq   = state[i] - qGoal[i];
            acc[i] = (-dVdq - damping * velocity[i]) / mass;
        }
        return acc;
    }

    // ─── ACTION INTEGRAL ─────────────────────────────────────────────────────

    /**
     * Numerically integrate S = ∫_{t1}^{t2} L(q, q̇, t) dt
     * using the Euler-Lagrange equations to propagate the trajectory.
     *
     * @param initialState  starting cognitive state q(t1)
     * @param goalState     target state q(t2)
     * @return action S along the optimal (Euler-Lagrange) trajectory
     */
    public double computeActionIntegral(double[] initialState, double[] goalState) {
        double[] sq    = initialState.clone();
        double[] sqDot = new double[dim];
        double   S     = 0.0;

        for (int step = 0; step < integrationSteps; step++) {
            double[] acc = eulerLagrangeAcceleration(sq, sqDot);
            double L     = lagrangian(sq, sqDot);
            S += L * dt;
            for (int i = 0; i < dim; i++) {
                sqDot[i] += acc[i] * dt;
                sq[i]    += sqDot[i] * dt;
            }
        }
        trajectoryComputes++;
        return S;
    }

    // ─── SINGLE STEP (LIVE INTEGRATION) ──────────────────────────────────────

    /**
     * Advance the internal state q by one Euler-Maruyama step.
     * Applies Euler-Lagrange dynamics: q̈ = −(q−q_goal) − damping·q̇
     * @return the optimal action vector (current acceleration)
     */
    public double[] step() {
        double[] acc = eulerLagrangeAcceleration(q, qDot);
        for (int i = 0; i < dim; i++) {
            qDot[i] += acc[i] * dt;
            q[i]    += qDot[i] * dt;
        }

        kineticEnergy   = kineticEnergy(qDot);
        potentialEnergy = potentialEnergy(q);
        lagrangian      = kineticEnergy - potentialEnergy;
        actionIntegral += lagrangian * dt;

        stepCount++;
        return acc;
    }

    /**
     * Run n steps of Euler-Lagrange integration.
     * @return final action vector
     */
    public double[] rollout(int steps) {
        double[] action = null;
        for (int i = 0; i < steps; i++) action = step();
        return action;
    }

    // ─── GOAL MANAGEMENT ─────────────────────────────────────────────────────

    public void setGoal(double[] goal) {
        System.arraycopy(goal, 0, qGoal, 0, Math.min(goal.length, dim));
        goalUpdates++;
    }

    public void setStateFromObservation(double[] obs) {
        System.arraycopy(obs, 0, q, 0, Math.min(obs.length, dim));
        java.util.Arrays.fill(qDot, 0.0);
    }

    // ─── ACCESSORS ───────────────────────────────────────────────────────────

    public double   getKineticEnergy()       { return kineticEnergy; }
    public double   getPotentialEnergy()     { return potentialEnergy; }
    public double   getLagrangian()          { return lagrangian; }
    public double   getActionIntegral()      { return actionIntegral; }
    public double[] getState()               { return q.clone(); }
    public double[] getVelocity()            { return qDot.clone(); }
    public double[] getGoal()                { return qGoal.clone(); }
    public int      getDim()                 { return dim; }
    public long     getStepCount()           { return stepCount; }
    public long     getGoalUpdates()         { return goalUpdates; }
    public long     getTrajectoryComputes()  { return trajectoryComputes; }

    /** Distance from current state to goal — proxy for Free Energy. */
    public double distanceToGoal() { return Math.sqrt(potentialEnergy * 2.0); }

    // ─── STATUS ──────────────────────────────────────────────────────────────

    public String getStatus() {
        return String.format(
            "LagrangianActionEngine | dim=%d | T=%.4f V=%.4f L=%.4f S=%.4f | " +
            "dist_goal=%.4f | steps=%d goals=%d traj=%d",
            dim, kineticEnergy, potentialEnergy, lagrangian, actionIntegral,
            distanceToGoal(), stepCount, goalUpdates, trajectoryComputes
        );
    }
}
