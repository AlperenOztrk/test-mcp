# Deployment Guide for Render

This guide explains how to deploy the KK Credit Card SSE application to Render.

## Prerequisites

1. Git repository with your code
2. Render account (free tier available)
3. Docker support (Render provides this)

## Deployment Steps

### Option 1: Using render.yaml (Recommended)

1. **Push to Git**: Make sure your code is in a Git repository (GitHub, GitLab, etc.)

2. **Connect to Render**:
   - Go to [Render Dashboard](https://dashboard.render.com/)
   - Click "New" → "Web Service"
   - Connect your Git repository

3. **Configure Service**:
   - Render will automatically detect the `render.yaml` file
   - The service will be configured automatically with:
     - Name: `kk-sse-credit-cards`
     - Environment: Docker
     - Port: 8080
     - Health check: `/api/cards`

4. **Deploy**: Click "Create Web Service" and Render will build and deploy your app

### Option 2: Manual Configuration

1. **Create Web Service**:
   - Go to Render Dashboard
   - Click "New" → "Web Service"
   - Connect your repository

2. **Configure Settings**:
   - **Name**: `kk-sse-credit-cards`
   - **Environment**: Docker
   - **Dockerfile Path**: `./Dockerfile`
   - **Port**: 8080

3. **Environment Variables**:
   - `PORT`: 8080
   - `JAVA_OPTS`: `-Xmx512m -Xms256m`

4. **Health Check**: Set to `/api/cards`

5. **Deploy**: Click "Create Web Service"

## Application Features

Once deployed, your application will provide:

### Server-Sent Events (SSE) Endpoints:
- `/api/cards/stream` - Real-time credit card updates
- `/api/applications/stream` - Live application processing updates

### REST API Endpoints:
- `GET /api/cards` - Get all credit cards
- `GET /api/cards/{id}` - Get specific credit card
- `POST /api/applications` - Submit credit card application
- `GET /api/bonuses` - Get card bonuses

### Web Interface:
- `/` - Interactive web interface with live updates
- Real-time interest rate changes
- Application submission form
- Live update logs

## Testing SSE Functionality

After deployment, you can test SSE streams using:

```bash
# Test card updates stream
curl -N -H "Accept: text/event-stream" https://your-app.onrender.com/api/cards/stream

# Test application updates stream
curl -N -H "Accept: text/event-stream" https://your-app.onrender.com/api/applications/stream
```

Or simply visit the web interface to see the live updates in action.

## Free Tier Limitations

Render's free tier includes:
- 750 hours/month (enough for continuous deployment)
- Auto-sleep after 15 minutes of inactivity
- 512MB RAM limit
- Shared CPU

The app is optimized for these constraints with:
- Minimal memory footprint
- Efficient SSE connection management
- Graceful connection cleanup

## Troubleshooting

### Common Issues:

1. **Build Failures**: 
   - Check that Java 21 is compatible
   - Verify Maven dependencies resolve correctly

2. **Memory Issues**:
   - App is configured with `-Xmx512m` for free tier
   - SSE connections are cleaned up automatically

3. **Port Issues**:
   - App uses `${PORT:8080}` to work with Render's dynamic ports

4. **Health Check Failures**:
   - Health check endpoint `/api/cards` should return HTTP 200
   - Check application logs in Render dashboard

## Monitoring

Monitor your application through:
- Render Dashboard logs
- Application metrics
- SSE connection counts in the web interface
- Health check status

## Scaling

To scale beyond the free tier:
- Upgrade to paid plan for always-on service
- Increase memory/CPU allocation
- Add horizontal scaling if needed 